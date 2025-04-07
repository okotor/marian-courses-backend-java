package com.tehacko.backend_java;

import com.tehacko.backend_java.model.Course;
import com.tehacko.backend_java.repo.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CourseRepo courseRepo;
    private final boolean isEnabled;

    @Autowired
    public DataLoader(CourseRepo courseRepo, @Value("${dataloader.enabled}") boolean isEnabled) {
        this.courseRepo = courseRepo;
        this.isEnabled = isEnabled;
    }

    @Override
    public void run(String... args) {
        if (isEnabled) {
            System.out.println("Running DataLoader...");
            loadDummyCourses();
        } else {
            System.out.println("DataLoader is disabled.");
        }
    }

    private void loadDummyCourses() {
        if (courseRepo.count() == 0) { // Avoid duplicate inserts
            List<Course> courses = List.of(
                    new Course(0, LocalDate.of(2025, 1, 1), "veronika-semi1",
                            "Jak digitalizovat a prodat ručně tvořené umění",
                            "veronika-vytvarna.png",
                            "Když máš talent, dej o sobě vědět!",
                            """
                            1. Úvod do digitalizace:
                               Základní principy a nástroje pro převedení fyzického umění do digitální podoby.
                            
                            2. Online platformy:
                               Kde a jak nejlépe prezentovat a prodávat umělecká díla.
                            
                            3. Marketing pro umělce:
                               Strategie propagace na sociálních sítích a tvorba osobní značky.
                            
                            4. Právní aspekty:
                               Ochrana autorských práv a licencování digitálního obsahu.
                            
                            5. Automatizace prodeje:
                               Jak si usnadnit práci pomocí online nástrojů a platforem.
                            """,
                            "Veronika Výtvarná", "vvytvarna@example.com"),

                    new Course(0, LocalDate.of(2024, 12, 31), "zoltan-webi1",
                            "Zdravý životní styl se Zoltánem",
                            "zoltan-zdravozivotnestylovy.png",
                            "Představujeme nejnovější a nejefektivnější metodu zdravého stravování.",
                            """
                            1. Zásady zdravého životního stylu:
                               Jak správně nastavit každodenní režim pro dlouhodobé zdraví.
                            
                            2. Výživa a stravovací návyky:
                               Jaké potraviny preferovat a jak je kombinovat.
                            
                            3. Pohyb a cvičení:
                               Doporučené aktivity a jejich přínos pro zdraví.
                            
                            4. Mentální pohoda:
                               Jak správně pracovat se stresem a vyvážit pracovní a osobní život.
                            
                            5. Dlouhodobá udržitelnost:
                               Jak si vytvořit zdravé návyky, které vydrží.
                            """,
                            "Zoltán Zdravoživotněstylový", "zozd@example.com"),

                    new Course(0, LocalDate.of(2024, 7, 18), "bedrich-vide1",
                            "Vstříc duchovnímu, fyzickému i finančnímu zdraví s B-Bedřichem",
                            "bedrich-businessosportovni.png",
                            "Získej rovnováhu ducha, duše i těla.",
                            """
                            1. Autogenní trénink:
                               Jak dosáhnout vnitřní rovnováhy a duševního klidu.
                            
                            2. Fyzická kondice:
                               Cvičení pro udržení zdraví a vitality.
                            
                            3. Finanční gramotnost:
                               Jak efektivně hospodařit s penězi a investovat.
                            
                            4. Psychologie úspěchu:
                               Jak si nastavit správné myšlení a dosáhnout svých cílů.
                            
                            5. Praktické aplikace:
                               Jak spojit teorii s praxí a využít získané znalosti v každodenním životě.
                            """,
                            "Bedřich Businessosportovní", "busibeda@example.com"),

                    new Course(0, LocalDate.of(2025, 1, 20),
                            "valerie-webi1", "Workshop líčení s novým revolučním nástrojem Make-U-Up",
                            "valerie-vynalezava.png",
                            "Buďte mezi tisícemi žen, které využívají revoluční metody líčení s Make-U-Up.",
                            "1. Úvod do moderního líčení:\n" +
                                    "   Jaké jsou nejnovější trendy a technologie v kosmetice.\n\n" +
                                    "2. Použití Make-U-Up:\n" +
                                    "   Jak správně používat nástroj pro dokonalý výsledek.\n\n" +
                                    "3. Tipy a triky profesionálů:\n" +
                                    "   Jak si přizpůsobit líčení podle tvaru obličeje a příležitosti.\n\n" +
                                    "4. Péče o pleť:\n" +
                                    "   Jak připravit pleť na líčení a udržet ji zdravou.\n\n" +
                                    "5. Praktický workshop:\n" +
                                    "   Možnost vyzkoušet si vše na vlastní kůži s odborným vedením.",
                            "Valerie Vynalézavá", "vevycko@example.com"),

                    new Course(0, LocalDate.of(2024, 3, 25),
                            "barbora-vide1", "Smart-Greenery: Software k údržbě a zalévání květin.",
                            "barbora-botanickozkoumava.png",
                            "Zjednodušte si údržbu zeleně v domácnosti a na zahradě automatizovaným nástrojem Smart-Greenery.",
                            "1. Úvod do Smart-Greenery:\n" +
                                    "   Jak software pomáhá s péčí o rostliny.\n\n" +
                                    "2. Nastavení systému:\n" +
                                    "   Jak nakonfigurovat zavlažování a údržbu.\n\n" +
                                    "3. Automatizace procesů:\n" +
                                    "   Jak optimalizovat zavlažování podle podmínek.\n\n" +
                                    "4. Mobilní aplikace:\n" +
                                    "   Jak kontrolovat stav rostlin na dálku.\n\n" +
                                    "5. Ekologické aspekty:\n" +
                                    "   Jak šetřit vodu a zlepšit růst rostlin.",
                            "Barbora Botanická", "barbota@example.com"),

                    new Course(0, LocalDate.of(2025, 2, 1),
                            "ursula-ursulova-audi1", "Umělecké projekty na univerzitách: Jak získat zakázky od designer firem",
                            "ursula-umeleckovedecka.png",
                            "Jak vyvolat a upevnit zájem designer firem k zadávání uměleckých zakázek u vás na univerzitě.",
                            "1. Příprava projektu:\n" +
                                    "   Jak vytvořit atraktivní umělecký návrh pro firmy.\n\n" +
                                    "2. Navázání kontaktů:\n" +
                                    "   Jak oslovit potenciální partnery a investory.\n\n" +
                                    "3. Prezentace a vyjednávání:\n" +
                                    "   Jak efektivně prezentovat svůj projekt a vyjednat podmínky spolupráce.\n\n" +
                                    "4. Realizace zakázky:\n" +
                                    "   Jak správně řídit proces tvorby a dodání projektu.\n\n" +
                                    "5. Udržení dlouhodobé spolupráce:\n" +
                                    "   Jak budovat pevné vztahy s firmami a získávat další zakázky.",
                            "Uršula Uměleckovědecká", "umeur@example.com")
            );

            courseRepo.saveAll(courses);
            System.out.println("Dummy courses inserted!");
        }
    }
}