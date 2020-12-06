package com.eoi.collector.common;

public interface BaseResult {

    default ResponseWrapper result(String code, String msg, Object entity, Integer total) {
        return ResponseWrapper.builder()
                .code(code)
                .msg(msg)
                .entity(entity)
                .total(total)
                .build();
    }

    default ResponseWrapper successful() {
        return result(MsgCode.success, "", null, 0);
    }

    default ResponseWrapper successful(Object entity) {
        return successful("", entity);
    }

    default ResponseWrapper successful(Object entity, int totalCount) {
        return result(MsgCode.success, "", entity, totalCount);
    }

    default ResponseWrapper successful(Object entity, Long totalCount) {
        return result(MsgCode.success, "", entity, totalCount.intValue());
    }

    default ResponseWrapper successful(String message, Object entity) {
        return result(MsgCode.success, message, entity, 0);
    }

    default ResponseWrapper failure(String code, String message) {
        return result(code, message, null, 0);
    }
}
