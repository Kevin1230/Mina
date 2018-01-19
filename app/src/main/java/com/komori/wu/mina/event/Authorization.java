package com.komori.wu.mina.event;

/**
 * Created by KomoriWu
 * on 2018-01-19.
 */

public class Authorization {

    /**
     * event : {"header":{"namespace":"Alexa.Authorization","name":"AcceptGrant.Response","payloadVersion":"3","messageId":"5f8a426e-01e4-4cc9-8b79-65f8bd0fd8a4"},"payload":{}}
     */

    private EventBean event;

    public EventBean getEvent() {
        return event;
    }

    public void setEvent(EventBean event) {
        this.event = event;
    }

    public static class EventBean {
        /**
         * header : {"namespace":"Alexa.Authorization","name":"AcceptGrant.Response","payloadVersion":"3","messageId":"5f8a426e-01e4-4cc9-8b79-65f8bd0fd8a4"}
         * payload : {}
         */

        private HeaderBean header;
        private PayloadBean payload;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public PayloadBean getPayload() {
            return payload;
        }

        public void setPayload(PayloadBean payload) {
            this.payload = payload;
        }

        public static class HeaderBean {
            /**
             * namespace : Alexa.Authorization
             * name : AcceptGrant.Response
             * payloadVersion : 3
             * messageId : 5f8a426e-01e4-4cc9-8b79-65f8bd0fd8a4
             */

            private String namespace;
            private String name;
            private String payloadVersion;
            private String messageId;

            public String getNamespace() {
                return namespace;
            }

            public void setNamespace(String namespace) {
                this.namespace = namespace;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPayloadVersion() {
                return payloadVersion;
            }

            public void setPayloadVersion(String payloadVersion) {
                this.payloadVersion = payloadVersion;
            }

            public String getMessageId() {
                return messageId;
            }

            public void setMessageId(String messageId) {
                this.messageId = messageId;
            }
        }

        public static class PayloadBean {
        }
    }
}
