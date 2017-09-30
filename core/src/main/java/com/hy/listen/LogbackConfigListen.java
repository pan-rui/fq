package com.hy.listen;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Administrator on 2015/8/31.
 */
public class LogbackConfigListen implements ServletContextListener {
/*    private static final Logger logger = LoggerFactory.getLogger(LogbackConfigListen.class);
    private static final String CONFIG_LOCATION="logbackConfigLocation";*/
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //从web.xml中加载指定文件名的日志配置文件
/*        String logbackConfigCation = servletContextEvent.getServletContext().getInitParameter(CONFIG_LOCATION);
//        String fn = servletContextEvent.getServletContext().getRealPath(logbackConfigCation);
        try {
//            UrlResource urlResource = new UrlResource(logbackConfigCation);
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(loggerContext);
//            String filePath = urlResource.getFile().getAbsolutePath();
            joranConfigurator.doConfigure(getClass().getClassLoader().getResourceAsStream(logbackConfigCation.split(":")[1]));
//            joranConfigurator.doConfigure(fn);
            logger.debug("loaded slf4j configure file from {}", logbackConfigCation);
            System.out.println("logback.xml路径=====>" + logbackConfigCation);
        } catch (JoranException e) {
            e.printStackTrace();
            logger.error("can loading slf4j configure file from " + logbackConfigCation, e);
        }*/

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
