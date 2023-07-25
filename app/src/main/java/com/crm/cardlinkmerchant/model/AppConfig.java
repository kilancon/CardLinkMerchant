package com.crm.cardlinkmerchant.model;

public class AppConfig {
    private AboutDetails about_details;
    private String name ="";
    public AppConfig(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AboutDetails getAbout_details() {
        return about_details;
    }

    public void setAbout_details(AboutDetails about_details) {
        this.about_details = about_details;
    }

    public static class AboutDetails{
        private About about;
        private TermsConditions terms_conditions;

        public AboutDetails(){

        };

        public About getAbout() {
            return about;
        }

        public void setAbout(About about) {
            this.about = about;
        }

        public TermsConditions getTerms_conditions() {
            return terms_conditions;
        }

        public void setTerms_conditions(TermsConditions terms_conditions) {
            this.terms_conditions = terms_conditions;
        }
    }

    public static class About{
        private String url;
        private String content;
        public About(){};

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class TermsConditions{
        private String url;
        private String content;
        public TermsConditions(){};

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
