package com.dront.sensors;

import java.util.Comparator;

public class AccRecordMeanComparator  implements Comparator<AccRecord> {
    @Override
    public int compare(AccRecord accRecord1, AccRecord accRecord2) {
        return (int)Math.ceil(accRecord1.getAbs() - accRecord2.getAbs());
    }
}