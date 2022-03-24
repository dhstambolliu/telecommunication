import menu.MainMenu;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();

        EntityManager entityManager = sessionFactory.createEntityManager();

        MainMenu mainMenu = new MainMenu();
        mainMenu.welcomeMenu();
    }
}
