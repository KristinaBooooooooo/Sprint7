package ru.practikum.kristinabogatova.clients.generators;

import ru.practikum.kristinabogatova.clients.models.Courier;
import ru.practikum.kristinabogatova.clients.models.Order;

import java.util.List;
import java.util.UUID;

public class TestDataGenerator {

    public static Courier getRandomCourier() {
        String uniq = UUID.randomUUID().toString().substring(0, 8);
        return new Courier("login_" + uniq, "pass_" + uniq, "Name_" + uniq);
    }

    public static Order getRandomOrder(List<String> colors) {
        String uniq = UUID.randomUUID().toString().substring(0, 5);
        String phone = "+7" + (9000000000L + Math.abs(uniq.hashCode()) % 1000000000L);
        return new Order(
                "First" + uniq,
                "Last" + uniq,
                "Address " + uniq,
                4,
                phone,
                3,
                "2026-06-06",
                "comment " + uniq,
                colors
        );
    }
}
