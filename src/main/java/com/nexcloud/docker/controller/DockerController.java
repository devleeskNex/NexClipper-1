package com.nexcloud.docker.controller;

import com.nexcloud.docker.DockerUtil;
import com.nexcloud.docker.stats.StatsRepository;
import com.nexcloud.docker.stats.StatsScheduler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by uengine on 2017. 8. 9..
 * <p>
 * Implementation Principles: - REST Maturity Level : 3 (Hateoas)
 * - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 * - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 * - json must be Typed JSON to enable object polymorphism
 * - need to change the jackson engine.
 * TODO: accept? typed json is sometimes hard to read
 */
@RestController
@RequestMapping("/docker")
public class DockerController {

    private static final Logger logger = LoggerFactory.getLogger(DockerController.class);

    @Autowired
    StatsRepository statsRepository;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT}, produces = "application/json;charset=UTF-8")
    public Object runApi(HttpServletRequest request, @RequestBody(required = false) String body
    ) throws Exception {

        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        return DockerUtil.procDockerApi(uri.replaceFirst("/docker", "") +
                        (StringUtils.isEmpty(queryString) ? "" : "?" + queryString),
                request.getMethod(),
                null,
                body);
    }

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/emitter")
    public SseEmitter handle(HttpServletRequest request,
                             HttpServletResponse response
    ) throws Exception {
        try {

            SseEmitter emitter = new SseEmitter(360_000L); //360 sec.
            emitters.add(emitter);

            emitter.onCompletion(() -> this.emitters.remove(emitter));
            emitter.onTimeout(() -> this.emitters.remove(emitter));

            logger.info("emitter counts: {} ", emitters.size());

            return emitter;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unauthorized");
            return null;
        }
    }

    public void emitterSend(Object object) throws Exception {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach(emitter -> {
            try {
                emitter.send(object);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}
