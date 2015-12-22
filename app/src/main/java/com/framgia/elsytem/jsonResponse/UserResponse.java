package com.framgia.elsytem.jsonResponse;

import java.util.List;

/**
 * Created by avishek on 12/17/15.
 */
public class UserResponse {

    /**
     * id : 106
     * name : Abu Khalid
     * email : khalid@gmail.com
     * admin : false
     * created_at : 2015-12-15T06:02:47.388Z
     * updated_at : 2015-12-18T10:18:42.106Z
     * avatar :
     * activities : [{"id":39,"content":"Sign up","created_at":"2015-12-15T06:02:47.594Z"},{"id":43,"content":"Login","created_at":"2015-12-15T06:23:21.723Z"},{"id":174,"content":"Login","created_at":"2015-12-17T05:08:21.362Z"},{"id":175,"content":"Login","created_at":"2015-12-17T05:14:50.476Z"},{"id":200,"content":"Login","created_at":"2015-12-17T10:21:20.553Z"},{"id":201,"content":"Login","created_at":"2015-12-17T10:29:43.516Z"},{"id":208,"content":"Login","created_at":"2015-12-17T11:09:45.336Z"},{"id":209,"content":"Login","created_at":"2015-12-17T11:14:05.295Z"},{"id":210,"content":"Login","created_at":"2015-12-17T11:15:10.753Z"},{"id":211,"content":"Login","created_at":"2015-12-17T11:39:37.738Z"},{"id":213,"content":"Login","created_at":"2015-12-17T12:02:56.451Z"},{"id":214,"content":"Login","created_at":"2015-12-17T12:05:09.822Z"},{"id":215,"content":"Login","created_at":"2015-12-17T12:07:25.468Z"},{"id":225,"content":"Login","created_at":"2015-12-17T12:40:28.973Z"},{"id":239,"content":"Login","created_at":"2015-12-18T10:18:42.119Z"}]
     */

    private UserEntity user;

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    public static class UserEntity {
        private int id;
        private String name;
        private String email;
        private boolean admin;
        private String created_at;
        private String updated_at;
        private String avatar;
        /**
         * id : 39
         * content : Sign up
         * created_at : 2015-12-15T06:02:47.594Z
         */

        private List<ActivitiesEntity> activities;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setActivities(List<ActivitiesEntity> activities) {
            this.activities = activities;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public boolean isAdmin() {
            return admin;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getAvatar() {
            return avatar;
        }

        public List<ActivitiesEntity> getActivities() {
            return activities;
        }

        public static class ActivitiesEntity {
            private int id;
            private String content;
            private String created_at;

            public void setId(int id) {
                this.id = id;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public int getId() {
                return id;
            }

            public String getContent() {
                return content;
            }

            public String getCreated_at() {
                return created_at;
            }
        }
    }
}
