package com.github.stormwyrm.eventbus.compiler;

import com.github.stormwyrm.eventbus.annotation.Subscribe;
import com.github.stormwyrm.eventbus.annotation.ThreadMode;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class EventBusAnnotationProcessor extends AbstractProcessor {
    private Types typeUtils;
    private Elements elementUtils;
    private Logger logger;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        logger = new Logger(processingEnv.getMessager());
        Map<String, String> options = processingEnv.getOptions();
        logger.info("options = " + options);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Subscribe.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() == 0)
            return false;

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Subscribe.class);
        return processAnnotationElemet(elements);
    }

    private boolean processAnnotationElemet(Set<? extends Element> elements) {
        //将类节点信息TypeElement对应的方法节点ExecutableElement保存
        LinkedHashMap<TypeElement, List<ExecutableElement>> executableElementsByClass = new LinkedHashMap<>();

        for (Element element : elements) {
            if (element instanceof ExecutableElement) {
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                if (isVisible(typeElement)) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    if (checkNoError(executableElement)) {
                        List<ExecutableElement> executableElements = executableElementsByClass.get(typeElement);
                        if (executableElements == null) {
                            executableElements = new ArrayList<>();
                            executableElementsByClass.put(typeElement, executableElements);
                        }
                        executableElements.add(executableElement);
                    }
                }
            } else {
                logger.error("@Subscribe annotatioin only allow apply method.", element);
                return false;
            }
        }

        //将对应信息生成类
        FieldSpec fieldSpec = FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(HashMap.class), TypeName.get(Class.class), TypeName.get(elementUtils.getTypeElement(Consts.SUBSCRIBE_INFO).asType())),
                "SUBSCRIBE_INFO_INDEX",
                Modifier.PRIVATE, Modifier.STATIC)
                .build();

        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(Consts.GENERATE_CLASS_NAME)
                .addSuperinterface(TypeName.get(elementUtils.getTypeElement(Consts.SUBSCRIBE_INFO_INDEX).asType()))
                .addField(fieldSpec)
                .addStaticBlock(CodeBlock.of("SUBSCRIBE_INFO_INDEX = new HashMap<>();"));

        MethodSpec.Builder constructMethodBuilder = MethodSpec.constructorBuilder();

        for (Map.Entry<TypeElement, List<ExecutableElement>> me : executableElementsByClass.entrySet()) {
            TypeElement typeElement = me.getKey();
            List<ExecutableElement> executableElements = me.getValue();

            CodeBlock.Builder codeBuilder = CodeBlock.builder();
            //addSubscribeInfo(MainActivity.class,new SimpleSubscribeInfo(MainActivity.class,new SubscribeMethodIn
            codeBuilder.add("addSubscribeInfo($T.class,\nnew $T($T.class,new $T[]{\n",
                    ClassName.get(typeElement),
                    ClassName.get(elementUtils.getTypeElement(Consts.SIMPLE_SUBSCRIBE_INFO)),
                    ClassName.get(typeElement),
                    ClassName.get(elementUtils.getTypeElement(Consts.SUBSCRIBE_METHOD_INFO))
            );
            int methodElemCount = executableElements.size();
            for (int i = 0; i < methodElemCount; i++) {
                ExecutableElement executableElement = executableElements.get(i);
                List<? extends VariableElement> parameters = executableElement.getParameters();
                VariableElement variableElement = parameters.get(0);
                TypeMirror paramType = getTypeMirror(variableElement);

                TypeElement paramElement = (TypeElement) typeUtils.asElement(paramType);

                String methodName = executableElement.getSimpleName().toString();//方法名
                Subscribe annotation = executableElement.getAnnotation(Subscribe.class);
                ThreadMode threadMode = annotation.threadMode();//线程
                Boolean sticky = annotation.isSticky();//是否是粘性

                //new SimpleSubscribeInfo(ThreadMode.MAIN,true,"methodName",Integer.class)
                codeBuilder.add("new $T($T.$L,$L,$S,$T.class),\n",
                        ClassName.get(elementUtils.getTypeElement(Consts.SUBSCRIBE_METHOD_INFO)),
                        ClassName.get(ThreadMode.class),
                        threadMode.name(),
                        sticky,
                        methodName,
                        ClassName.get(paramElement)
                );

            }

            codeBuilder.add("}));\n");
            constructMethodBuilder.addCode(codeBuilder.build());
        }


        MethodSpec getSubscribeInfoMethod = MethodSpec.methodBuilder("getSubscribeInfo")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(elementUtils.getTypeElement(Consts.SUBSCRIBE_INFO)))
                .addParameter(ClassName.get(Class.class), "subscriberClass")
                .addStatement("return SUBSCRIBE_INFO_INDEX.get(subscriberClass)")
                .build();


        MethodSpec addSubscribeInfoMethod = MethodSpec.methodBuilder("addSubscribeInfo")
                .addModifiers(Modifier.PRIVATE)
                .returns(void.class)
                .addParameter(ClassName.get(Class.class), "subscriberClass")
                .addParameter(ClassName.get(elementUtils.getTypeElement(Consts.SUBSCRIBE_INFO)), "subscribeInfo")
                .addCode(CodeBlock.builder()
                        .addStatement("SUBSCRIBE_INFO_INDEX.put(subscriberClass,subscribeInfo)")
                        .build())
                .build();

        try {
            JavaFile.builder(
                    Consts.PACKAGE_NAME,
                    typeSpec.addMethod(constructMethodBuilder.build())
                            .addMethod(getSubscribeInfoMethod)
                            .addMethod(addSubscribeInfoMethod)
                            .build()
            ).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private TypeMirror getTypeMirror(VariableElement variableElement) {
        TypeMirror paramType = variableElement.asType();
        if (paramType instanceof TypeVariable) {
            TypeMirror upperBound = ((TypeVariable) paramType).getUpperBound();
            if (upperBound instanceof DeclaredType) {
                paramType = upperBound;
                logger.info("Using upper bound type " + upperBound +
                        " for generic parameter", variableElement);
            }
        }
        return paramType;
    }


    //检查方法是否可见，以及是否有错误
    private boolean checkNoError(ExecutableElement executableElement) {
        Set<Modifier> modifiers = executableElement.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC)) {
            logger.info("@subscribe annotaiton " + executableElement.getSimpleName() + " method must be public", executableElement);
            return false;
        }

        if (modifiers.contains(Modifier.STATIC)) {
            logger.info("@subscribe annotaiton " + executableElement.getSimpleName() + " method cannot be static", executableElement);
            return false;
        }

        List<? extends VariableElement> parameters = executableElement.getParameters();
        if (parameters.size() > 1) {
            logger.info("@subscribe annotaiton " + executableElement.getSimpleName() + " method only allow a param", executableElement);
            return false;
        }

        String skipReason = null;
        TypeMirror typeMirror = getTypeMirror(executableElement.getParameters().get(0));
        if (!(typeMirror instanceof DeclaredType) ||
                !(((DeclaredType) typeMirror).asElement() instanceof TypeElement)) {
            skipReason = "event type cannot be processed";
        }
        if (skipReason == null) {
            TypeElement eventTypeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
            if (!isVisible(eventTypeElement)) {
                skipReason = "event type is not public";
            }
        }
        if (skipReason != null) {
            String msg = "Falling back to reflection because " + skipReason;
            msg += " (found in super class for " + executableElement + ")";
            logger.info(msg, executableElement);
            return false;
        }
        return true;
    }

    //检查类是否可见
    private boolean isVisible(TypeElement typeElement) {
        Set<Modifier> modifiers = typeElement.getModifiers();
        boolean visible;
        if (modifiers.contains(Modifier.PUBLIC)) {
            visible = true;
        } else if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.PROTECTED)) {
            visible = false;
            logger.error("Annotation class ：" + typeElement.getSimpleName() + " method cannot be private or protected", typeElement);
        } else {
            String subscriberPackage = getPackageElement(typeElement).getQualifiedName().toString();
            visible = Consts.PACKAGE_NAME.equals(subscriberPackage);
            if (visible) {
                logger.error("Annotation class ：" + typeElement.getSimpleName() + " cannot be private or protected", typeElement);
            }
        }
        return visible;
    }

    private PackageElement getPackageElement(TypeElement subscriberClass) {
        Element candidate = subscriberClass.getEnclosingElement();
        while (!(candidate instanceof PackageElement)) {
            candidate = candidate.getEnclosingElement();
        }
        return (PackageElement) candidate;
    }


}
