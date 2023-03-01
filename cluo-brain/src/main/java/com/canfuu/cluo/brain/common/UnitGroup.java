package com.canfuu.cluo.brain.common;

import java.util.List;

public interface UnitGroup {

    Unit chooseUnit(boolean linkFar);

    void addUnit(Unit unit);

    List<Unit> getAllUnit();
}
