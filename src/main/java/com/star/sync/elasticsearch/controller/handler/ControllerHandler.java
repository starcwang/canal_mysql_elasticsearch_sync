package com.star.sync.elasticsearch.controller.handler;

import com.star.sync.elasticsearch.model.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-31 20:55:00
 */
@ControllerAdvice
public class ControllerHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

    @ExceptionHandler
    @ResponseBody
    public Object exceptionHandler(Exception e, HttpServletResponse response) {
        logger.error("unknown_error", e);
        return new Response<>(2, e.getMessage(), null).toString();
    }
}
