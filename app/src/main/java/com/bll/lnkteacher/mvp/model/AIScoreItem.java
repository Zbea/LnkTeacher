package com.bll.lnkteacher.mvp.model;

import java.io.Serializable;
import java.util.List;

public class AIScoreItem implements Serializable {
    public ResultItem result;

    public static class ResultItem implements Serializable {
        public List<MessageItem> choices;

        public static class MessageItem implements Serializable{
            public ContentItem message;

            public static class ContentItem implements Serializable{
                public String content;
            }
        }
    }
}







