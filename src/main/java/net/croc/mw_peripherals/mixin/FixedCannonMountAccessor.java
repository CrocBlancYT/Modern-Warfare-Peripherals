package net.croc.mw_peripherals.mixin;

import net.croc.mw_peripherals.mixinducks.CannonMountDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;
import riftyboi.cbcmodernwarfare.cannon_control.compact_mount.CompactCannonMountBlockEntity;

@Pseudo
@Mixin({FixedCannonMountBlockEntity.class})
public interface FixedCannonMountAccessor extends CannonMountDuck {
  @Invoker(value = "assemble", remap = false)
  void Iassemble();

  @Invoker(value = "disassemble", remap = false)
  void Idisassemble();

  @Invoker(value = "tick", remap = false)
  void Itick();

  @Accessor(remap = false)
  float getCannonYaw();

  @Accessor(remap = false)
  float getCannonPitch();

  @Accessor(remap = false)
  void setCannonPitch(float pitch);

  @Accessor(remap = false)
  void setCannonYaw(float yaw);

  @Invoker(value = "getContraption", remap = false)
  PitchOrientedContraptionEntity IgetContraption();

  @Invoker(value = "onRedstoneUpdate", remap = false)
  void IonRedstoneUpdate(boolean a, boolean b, boolean c, boolean d, int e);
}