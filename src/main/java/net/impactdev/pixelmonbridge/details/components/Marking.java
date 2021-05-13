package net.impactdev.pixelmonbridge.details.components;

import com.google.common.collect.Maps;
import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JObject;
import net.impactdev.pixelmonbridge.details.PixelmonSource;

import java.util.Map;
import java.util.Optional;

public class Marking implements Writable<JObject> {

    private Mark mark;
    private PixelmonSource source;

    public Marking(Mark mark, PixelmonSource source) {
        this.mark = mark;
        this.source = source;
    }

    public static Optional<Marking> createFor(PixelmonSource source, int index) {
        for(Mark mark : Mark.values()) {
            if(mark.getIndexes().get(source) == index) {
                return Optional.of(new Marking(mark, source));
            }
        }

        return Optional.empty();
    }

    public Mark getMark() {
        return mark;
    }

    public PixelmonSource getSource() {
        return source;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("source", this.source.name())
                .add("ordinal", this.mark.getIndexes().get(this.source));
    }

    public enum Mark {
        LunchTime(18, 3),
        SleepyTime(19, 5),
        Dusk(20, 4),
        Dawn(21, 2),
        Cloudy(22, 10),
        Rainy(23, 7),
        Stormy(24, 9),
        Snowy(25, 12),
        Blizzard(26, 11),
        Dry(27, 13),
        Sandstorm(28, 6),
        Misty(29, 8),
        Destiny(30, 43),
        Fishing(31, 44),
        Curry(32, 45),
        Uncommon(33, 42),
        Rare(34, 1),
        Rowdy(35, 14),
        AbsentMinded(36, 15),
        Jittery(37, 16),
        Excited(38, 17),
        Charismatic(39, 18),
        Calmness(40, 19),
        Intense(41, 20),
        ZonedOut(42, 21),
        Joyful(43, 22),
        Angry(44, 23),
        Smiley(45, 24),
        Teary(46, 25),
        Upbeat(47, 26),
        Peeved(48, 27),
        Intellectual(49, 28),
        Ferocious(50, 29),
        Crafty(51, 30),
        Scowling(52, 31),
        Kindly(53, 32),
        Flustered(54, 33),
        PumpedUp(55, 34),
        ZeroEnergy(56, 35),
        Prideful(57, 36),
        Unsure(58, 37),
        Humble(59, 38),
        Thorny(60, 39),
        Vigor(61, 40),
        Slump(62, 41),
        ;

        private final Map<PixelmonSource, Integer> indexes = Maps.newHashMap();

        Mark(int reforged, int generations) {
            indexes.put(PixelmonSource.Reforged, reforged);
            indexes.put(PixelmonSource.Generations, generations);
        }

        public Map<PixelmonSource, Integer> getIndexes() {
            return indexes;
        }
    }

}
