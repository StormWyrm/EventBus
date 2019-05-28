package com.github.stormwyrm.eventbus.poster;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/22
 * Desc:
 **/
public class PendingPostQueue {
    PendingPost head;
    PendingPost tail;

    public synchronized void enqueue(PendingPost pendingPost) {
        if (pendingPost == null) {
            throw new NullPointerException("pendingPost is not allow empty");
        }
        if (head == null) {
            head = tail = pendingPost;
        } else {
            tail.next = pendingPost;
            tail = pendingPost;
        }
    }

    public synchronized PendingPost poll() {
        PendingPost pendingPost = head;
        if (head != null) {
            head = head.next;
            if(head == null){
                tail = null;
            }
        }
        return pendingPost;
    }
}
