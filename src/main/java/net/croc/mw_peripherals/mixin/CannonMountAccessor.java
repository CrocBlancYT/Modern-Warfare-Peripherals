package net.croc.mw_peripherals.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

@Pseudo
@Mixin({CannonMountBlockEntity.class})
public interface CannonMountAccessor {
  @Invoker(value = "assemble", remap = false)
  void Iassemble();

  @Invoker(value = "disassemble", remap = false)
  void Idisassemble();

  @Invoker(value = "tick", remap = false)
  void Itick();

  @Invoker(value = "getMaxDepress", remap = false)
  float IgetMaxDepress();
  
  @Invoker(value = "getMaxElevate", remap = false)
  float IgetMaxElevate();

  @Accessor(remap = false)
  float getCannonYaw();

  @Accessor(remap = false)
  float getCannonPitch();

  @Invoker(value = "setPitch", remap = false)
  void IsetPitch(float pitch);

  @Invoker(value = "setYaw", remap = false)
  void IsetYaw(float yaw);

  @Invoker(value = "getContraption", remap = false)
  PitchOrientedContraptionEntity IgetContraption();

  @Invoker(value = "onRedstoneUpdate", remap = false)
  void IonRedstoneUpdate(boolean a, boolean b, boolean c, boolean d, int e);
}