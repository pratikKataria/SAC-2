package com.caringaide.user.model;

public class Services {
        private String id;
        private String type;
        private String description;
        private String subscriptionFee;
        private String created;
        private String modified;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSubscriptionFee() {
            return subscriptionFee;
        }

        public void setSubscriptionFee(String subscriptionFee) {
            this.subscriptionFee = subscriptionFee;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }

}
