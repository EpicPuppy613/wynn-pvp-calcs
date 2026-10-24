package dev.epicpuppy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * A single damage controller for a player.
 * One of these will be made for every player participating in PvP.
 */
public class PlayerDamage {
    public final ArrayList<DamageBlock> blocks = new ArrayList<>();

    public void tick(long t) {
        blocks.removeIf(damageBlock -> damageBlock.tick(t));
    }

    public double dealDamage(double totalRawDamage, long t) {
        if (blocks.isEmpty() || blocks.getFirst().back > 0) {
            blocks.addFirst(new DamageBlock());
        }

        double damageLeft = totalRawDamage;
        double scaledDamage = 0;

        for (int i = 0; i < blocks.size(); i++) {
            DamageBlock block = blocks.get(i);
            if (i >= blocks.size() - 1 || blocks.get(i + 1).back > block.front + damageLeft) {
                scaledDamage += block.expand(damageLeft, t + 20);
                break;
            } else if (blocks.get(i + 1).back - block.front > 0) {
                damageLeft -= blocks.get(i + 1).back - block.front;
                scaledDamage += block.expand(blocks.get(i + 1).back - block.front, t + 20);
            }
        }

        return scaledDamage;
    }


    public static double damageCurve(double start, double end) {
        // Essentially using the FTC to determine damage dealt given the damage window before and after the hit
        return damageFunction(end) - damageFunction(start);
    }

    private static double damageFunction(double x) {
        return Math.sqrt(x) / 2.5d;
    }

    public static class DamageBlock {
        private double front = 0;
        private double back = 0;
        private final Deque<Double> damageValues = new ArrayDeque<>();
        private final Deque<Long> damageFades = new ArrayDeque<>();

        private double expand(double damage, long expires) {
            double scaledDamage = damageCurve(front, front + damage);
            front += damage;
            damageValues.add(damage);
            damageFades.add(expires);
            return scaledDamage;
        }

        public boolean tick(long t) {
            while (!damageFades.isEmpty() && damageFades.getFirst() < t) {
                damageFades.removeFirst();
                back += damageValues.removeFirst();
            }
            return damageValues.isEmpty();
        }
    }
}
