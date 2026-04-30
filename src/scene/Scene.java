package scene;

import lighting.AmbientLight;
import primitives.Color;
import geometries.impl.Geometries;

/**
 * Scene class holding all the physical elements of the 3D scene.
 * This is a Passive Data Structure (PDS) with public fields.
 */
public class Scene {
    public String name;
    public Color background = Color.BLACK;
    public AmbientLight ambientLight = AmbientLight.NONE;
    public Geometries geometries = new Geometries();

    /**
     * Constructor receiving only the scene name.
     * @param name Name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}