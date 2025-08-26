package com.bll.lnkteacher.mvp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AiRequestItem {
    public String type="vision";
    public String subject;
    public String prompt;
    public List<ImageItem> question=new ArrayList<>();
    public List<ImageItem> answer=new ArrayList<>();

    public static class ImageItem implements Serializable {
        public String type="image_url";
        public ImageUrlItem image_url;

        public static class ImageUrlItem implements Serializable{
            public String url;
        }
    }

}

