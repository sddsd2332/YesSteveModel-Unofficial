package com.fox.ysmu.geckolib3.geo;

import com.fox.ysmu.Config;
import com.fox.ysmu.geckolib3.core.util.Color;
import com.fox.ysmu.geckolib3.geo.render.built.*;
import com.fox.ysmu.geckolib3.model.provider.GeoModelProvider;
import com.fox.ysmu.geckolib3.util.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;

public interface IGeoRenderer<T> {

    public static MatrixStack MATRIX_STACK = new MatrixStack();

    default void render(GeoModel model, T animatable, float partialTicks, float red, float green, float blue,
                        float alpha) {
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        renderEarly(model, animatable, partialTicks, red, green, blue, alpha);

        renderLate(model, animatable, partialTicks, red, green, blue, alpha);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder builder = tess.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        //tess.startDrawing(GL11.GL_QUADS);// , DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        // Render all top level bones
        for (GeoBone group : model.topLevelBones) {
            renderRecursively(builder, animatable, group, red, green, blue, alpha);
        }

        tess.draw();

        renderAfter(model, animatable, partialTicks, red, green, blue, alpha);
        // GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    default boolean isBoneRenderOverriden(T animatable, GeoBone bone) {
        return false;
    }

    default void drawOverridenBone(T animatable, GeoBone bone) {

    }

    default void renderRecursively(BufferBuilder builder, T animatable, GeoBone bone, float red, float green, float blue,
                                   float alpha) {
        MATRIX_STACK.push();

        MATRIX_STACK.translate(bone);
        MATRIX_STACK.moveToPivot(bone);
        MATRIX_STACK.rotate(bone);
        MATRIX_STACK.scale(bone);
        MATRIX_STACK.moveBackFromPivot(bone);

        if (isBoneRenderOverriden(animatable, bone)) {
            drawOverridenBone(animatable, bone);
            MATRIX_STACK.pop();
            return;
        }

        if (!bone.isHidden()) {
            for (GeoCube cube : bone.childCubes) {
                MATRIX_STACK.push();
                GlStateManager.pushMatrix();
                try {
                    renderCube(builder, cube, red, green, blue, alpha);
                } catch (Exception e) {
                    if (Config.debugPrintStacktraces) {
                        e.printStackTrace();
                    }
                } finally {
                    GlStateManager.popMatrix();
                    MATRIX_STACK.pop();
                }
            }
        }
        if (!bone.childBonesAreHiddenToo()) {
            for (GeoBone childBone : bone.childBones) {
                renderRecursively(builder, animatable, childBone, red, green, blue, alpha);
            }
        }

        MATRIX_STACK.pop();
    }

    default void renderCube(BufferBuilder builder, GeoCube cube, float red, float green, float blue, float alpha) {
        MATRIX_STACK.moveToPivot(cube);
        MATRIX_STACK.rotate(cube);
        MATRIX_STACK.moveBackFromPivot(cube);

        boolean flat = cube.size.x == 0 || cube.size.y == 0 || cube.size.z == 0;
        if (flat) {
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(-1.0F, -10.0F);
        }

        for (GeoQuad quad : cube.quads) {
            if (quad == null) continue;
            Vector3f normal = new Vector3f(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());

            MATRIX_STACK.getNormalMatrix()
                    .transform(normal);

            /*
             * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
             */
            if ((cube.size.y == 0 || cube.size.z == 0) && normal.x < 0) {
                normal.x *= -1;
            }
            if ((cube.size.x == 0 || cube.size.z == 0) && normal.y < 0) {
                normal.y *= -1;
            }
            if ((cube.size.x == 0 || cube.size.y == 0) && normal.z < 0) {
                normal.z *= -1;
            }

            for (GeoVertex vertex : quad.vertices) {
                Vector4f vector4f = new Vector4f(vertex.position.x, vertex.position.y, vertex.position.z, 1.0F);

                MATRIX_STACK.getModelMatrix().transform(vector4f);
                builder.pos(vector4f.x, vector4f.y, vector4f.z).tex(vertex.textureU, vertex.textureV).color(red, green, blue, alpha).normal(normal.x, normal.y, normal.z).endVertex();

            }
        }

        if (flat) {
            GlStateManager.disablePolygonOffset();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
        }
    }

    /*
     * (-0.4095761, 0.5882118, 0.70710677, 1.0)
     * (0.409576, 1.1617882, 0.70710677, 1.0)
     * (0.0039961934, 1.7410161, -5.9604645E-8, 1.0)
     * (-0.81515586, 1.1674397, -5.9604645E-8, 1.0)
     * (-0.003996223, 0.008983791, -5.9604645E-8, 1.0)
     * (0.81515586, 0.5825603, -5.9604645E-8, 1.0)
     * (0.40957603, 1.1617882, -0.7071068, 1.0)
     * (-0.40957603, 0.5882118, -0.7071068, 1.0)
     */
    @SuppressWarnings("rawtypes")
    GeoModelProvider getGeoModelProvider();

    ResourceLocation getTextureLocation(T instance);

    @Nullable
    default GeoModel getGeoModel() {
        return null;
    }

    default void renderEarly(GeoModel model, T animatable, float ticks, float red, float green, float blue,
                             float alpha) {
        float width = getWidthScale(animatable);
        float height = getHeightScale(animatable);
        MATRIX_STACK.push();
        MATRIX_STACK.scale(width, height, width);
    }

    default void renderLate(GeoModel model, T animatable, float ticks, float red, float green, float blue,
                            float alpha) {
    }

    default void renderAfter(GeoModel model, T animatable, float ticks, float red, float green, float blue,
                             float alpha) {
        MATRIX_STACK.pop();
    }

    default Color getRenderColor(T animatable, float partialTicks) {
        return Color.ofRGBA(255, 255, 255, 255);
    }

    default Integer getUniqueID(T animatable) {
        return animatable.hashCode();
    }

    default GeoBone[] getPathFromRoot(GeoBone bone) {
        ArrayList<GeoBone> bones = new ArrayList<>();
        while (bone != null) {
            bones.add(0, bone);
            bone = bone.parent;
        }
        return bones.toArray(new GeoBone[0]);
    }

    default float getWidthScale(T animatable) {
        return 1F;
    }

    default float getHeightScale(T entity) {
        return 1F;
    }
}
