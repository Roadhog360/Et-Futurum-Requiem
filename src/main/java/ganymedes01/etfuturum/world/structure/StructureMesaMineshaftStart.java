package ganymedes01.etfuturum.world.structure;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureMineshaftStart;

import java.util.Random;

public class StructureMesaMineshaftStart extends StructureMineshaftStart {
	public StructureMesaMineshaftStart() {
	}

	public StructureMesaMineshaftStart(World p_i2039_1_, Random p_i2039_2_, int p_i2039_3_, int p_i2039_4_) {
		this.field_143024_c = p_i2039_3_; // chunkPosX
		this.field_143023_d = p_i2039_4_; // chunkPosZ
		StructureMesaMineshaftPieces.MesaRoom room = new StructureMesaMineshaftPieces.MesaRoom(0, p_i2039_2_, (p_i2039_3_ << 4) + 2, (p_i2039_4_ << 4) + 2);
		this.components.add(room);
		room.buildComponent(room, this.components, p_i2039_2_);

		this.updateBoundingBox();

		int j = 63 - this.boundingBox.maxY + this.boundingBox.getYSize() / 2 - -5;
		this.boundingBox.offset(0, j, 0);

		for (StructureComponent structurecomponent : this.components) {
			if (structurecomponent instanceof StructureMesaMineshaftPieces.Piece) {
				((StructureMesaMineshaftPieces.Piece) structurecomponent).offset(0, j, 0);
			}
		}
	}

}
