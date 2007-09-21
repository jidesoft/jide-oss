package com.jidesoft.converter;

import junit.framework.TestCase;

public class TestEnumConverter extends TestCase {
    public enum Rank {
        DEUCE, THREE, FOUR, FIVE, SIX,
        SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }

    public void testEnumConverterEnum() {
        ObjectConverter converter1 = new EnumConverter("Rank", Rank.values(), new String[]{
                Rank.DEUCE.toString(),
                Rank.THREE.toString(),
                Rank.FOUR.toString(),
                Rank.FIVE.toString(),
                Rank.SIX.toString(),
                Rank.SEVEN.toString(),
                Rank.EIGHT.toString(),
                Rank.NINE.toString(),
                Rank.TEN.toString(),
                Rank.JACK.toString(),
                Rank.QUEEN.toString(),
                Rank.KING.toString(),
                Rank.ACE.toString(),
        });

        ObjectConverter converter2 = new EnumConverter("Rank", Rank.values(), EnumConverter.toStrings(Rank.values()));

        assertEquals(Rank.DEUCE.toString(), converter2.toString(Rank.DEUCE, null));
    }
}
