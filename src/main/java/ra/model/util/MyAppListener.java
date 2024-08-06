package ra.model.util;

import org.springframework.context.ApplicationListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class MyAppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Ứng dụng khởi động
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Ứng dụng dừng
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("Deregistered JDBC driver: " + driver);
            } catch (Exception e) {
                System.err.println("Error deregistering JDBC driver: " + driver);
                e.printStackTrace();
            }
        }
    }
}
