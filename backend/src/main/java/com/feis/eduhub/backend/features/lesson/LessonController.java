package com.feis.eduhub.backend.features.lesson;

import java.util.List;

import com.feis.eduhub.backend.common.dto.ResponseDto;
import com.feis.eduhub.backend.common.exceptions.ValidationException;
import com.feis.eduhub.backend.common.interfaces.EndpointsRegister;
import com.feis.eduhub.backend.common.lib.AppEndpoint;
import com.feis.eduhub.backend.features.lessonAttendance.dto.LessonDto;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class LessonController implements EndpointsRegister {
    private final String BASE_V1_URL = AppEndpoint.DEFAULT_V1.getBaseUrl() + AppEndpoint.LESSON.getBaseUrl();
    private final LessonService lessonService;
    private final LessonMiddleware lessonMiddleware;

    public LessonController() {
        lessonService = new LessonService();
        lessonMiddleware = new LessonMiddleware();
    }

    @Override
    public void registerEndpoints(Javalin app) {
        lessonMiddleware.registerEndpoints(app);

        app.get(BASE_V1_URL, lessonsListHandler());
        app.get(BASE_V1_URL + "/{lessonId}", lessonInfoHandler());
    }

    private Handler lessonsListHandler() {
        return (ctx) -> {
            long accountId = ctx.attribute("accountId");
            List<LessonDto> lessonsList = lessonService.getLessonsByAccountId(accountId);
            ResponseDto<List<LessonDto>> response = new ResponseDto.ResponseBuilder<List<LessonDto>>(200)
                    .withMessage(String.format("Found %d entries", lessonsList.size()))
                    .withData(lessonsList)
                    .build();
            ctx.status(200).json(response);
        };
    }

    private Handler lessonInfoHandler() {
        return (ctx) -> {
            Long lessonId;
            try {
                lessonId = Long.valueOf(ctx.pathParam("lessonId"));
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid lessonId", e);
            }
            if (lessonId <= 0) {
                throw new ValidationException("Invalid lessonId");
            }
            Lesson lesson = lessonService.getLessonById(lessonId);
            ResponseDto<Lesson> response = new ResponseDto.ResponseBuilder<Lesson>(200)
                    .withMessage("Success")
                    .withData(lesson)
                    .build();
            ctx.status(200).json(response);
        };
    }
}
