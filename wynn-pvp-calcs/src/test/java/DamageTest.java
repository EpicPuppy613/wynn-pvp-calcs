import dev.epicpuppy.PlayerDamage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DamageTest {
    static double damageDealt(double damage) {
        return Math.sqrt(damage) / 2.5;
    }

    @Test
    void testSimpleBurst() {
        PlayerDamage damage = new PlayerDamage();
        double scaledDamage = 0;

        scaledDamage += damage.dealDamage(10_000, 0);
        scaledDamage += damage.dealDamage(20_000, 1);
        scaledDamage += damage.dealDamage(30_000, 2);
        scaledDamage += damage.dealDamage(40_000, 3);
        scaledDamage += damage.dealDamage(50_000, 4);

        assertEquals(damageDealt(150_000), scaledDamage);
    }

    @Test
    void testHitExpiration() {
        PlayerDamage damage = new PlayerDamage();
        double scaledDamage = 0;

        scaledDamage += damage.dealDamage(25_000, 0);

        damage.tick(21);

        scaledDamage += damage.dealDamage(25_000, 21);

        assertEquals(2 * damageDealt(25_000), scaledDamage);
    }

    @Test
    void testBlockOverflow() {
        PlayerDamage damage = new PlayerDamage();
        double scaledDamage = 0;

        scaledDamage += damage.dealDamage(10_000, 0);
        scaledDamage += damage.dealDamage(40_000, 1);

        damage.tick(21);

        scaledDamage += damage.dealDamage(20_000, 21);

        assertEquals(damageDealt(60_000) + damageDealt(10_000), scaledDamage);
    }
}
