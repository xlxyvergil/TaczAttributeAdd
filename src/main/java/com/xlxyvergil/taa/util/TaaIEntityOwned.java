package com.xlxyvergil.taa.util;

import net.minecraft.world.entity.LivingEntity;

public interface TaaIEntityOwned {

    public LivingEntity taaGetOwner();

    public void taaSetOwner(LivingEntity taaOwner);

}