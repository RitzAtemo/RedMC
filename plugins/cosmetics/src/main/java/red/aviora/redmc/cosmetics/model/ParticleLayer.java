package red.aviora.redmc.cosmetics.model;

public class ParticleLayer {

    public static final int COUNT_MIN = 1, COUNT_MAX = 50;
    public static final double SPEED_MIN = 0.0, SPEED_MAX = 5.0;
    public static final double YOFFSET_MIN = -5.0, YOFFSET_MAX = 5.0;
    public static final int TICKRATE_MIN = 1, TICKRATE_MAX = 100;
    public static final double RADIUS_MIN = 0.1, RADIUS_MAX = 10.0;
    public static final int POINTS_MIN = 1, POINTS_MAX = 128;
    public static final double OFFSET_MIN = -5.0, OFFSET_MAX = 5.0;
    public static final float DUSTSIZE_MIN = 0.1f, DUSTSIZE_MAX = 10.0f;
    public static final int COLOR_MIN = 0, COLOR_MAX = 255;

    private String particle = "FLAME";
    private ParticleShape shape = ParticleShape.POINT;
    private int count = 3;
    private double speed = 0.05;
    private double offsetX = 0.1;
    private double offsetY = 0.1;
    private double offsetZ = 0.1;
    private double yOffset = 0.1;
    private int tickRate = 1;
    private double shapeRadius = 1.0;
    private int shapePoints = 16;
    private int dustColorR = 255;
    private int dustColorG = 255;
    private int dustColorB = 255;
    private int dustColorToR = 255;
    private int dustColorToG = 255;
    private int dustColorToB = 255;
    private float dustSize = 1.0f;

    public ParticleLayer() {}

    public ParticleLayer(ParticleLayer other) {
        this.particle = other.particle;
        this.shape = other.shape;
        this.count = other.count;
        this.speed = other.speed;
        this.offsetX = other.offsetX;
        this.offsetY = other.offsetY;
        this.offsetZ = other.offsetZ;
        this.yOffset = other.yOffset;
        this.tickRate = other.tickRate;
        this.shapeRadius = other.shapeRadius;
        this.shapePoints = other.shapePoints;
        this.dustColorR = other.dustColorR;
        this.dustColorG = other.dustColorG;
        this.dustColorB = other.dustColorB;
        this.dustColorToR = other.dustColorToR;
        this.dustColorToG = other.dustColorToG;
        this.dustColorToB = other.dustColorToB;
        this.dustSize = other.dustSize;
    }

    public String getParticle() { return particle; }
    public void setParticle(String particle) { this.particle = particle; }

    public ParticleShape getShape() { return shape; }
    public void setShape(ParticleShape shape) { this.shape = shape; }

    public int getCount() { return count; }
    public void setCount(int count) {
        if (count < COUNT_MIN || count > COUNT_MAX) throw new IllegalArgumentException(count + " (range: " + COUNT_MIN + "-" + COUNT_MAX + ")");
        this.count = count;
    }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) {
        if (speed < SPEED_MIN || speed > SPEED_MAX) throw new IllegalArgumentException(speed + " (range: " + SPEED_MIN + "-" + SPEED_MAX + ")");
        this.speed = speed;
    }

    public double getOffsetX() { return offsetX; }
    public void setOffsetX(double offsetX) {
        if (offsetX < OFFSET_MIN || offsetX > OFFSET_MAX) throw new IllegalArgumentException(offsetX + " (range: " + OFFSET_MIN + "-" + OFFSET_MAX + ")");
        this.offsetX = offsetX;
    }

    public double getOffsetY() { return offsetY; }
    public void setOffsetY(double offsetY) {
        if (offsetY < OFFSET_MIN || offsetY > OFFSET_MAX) throw new IllegalArgumentException(offsetY + " (range: " + OFFSET_MIN + "-" + OFFSET_MAX + ")");
        this.offsetY = offsetY;
    }

    public double getOffsetZ() { return offsetZ; }
    public void setOffsetZ(double offsetZ) {
        if (offsetZ < OFFSET_MIN || offsetZ > OFFSET_MAX) throw new IllegalArgumentException(offsetZ + " (range: " + OFFSET_MIN + "-" + OFFSET_MAX + ")");
        this.offsetZ = offsetZ;
    }

    public double getYOffset() { return yOffset; }
    public void setYOffset(double yOffset) {
        if (yOffset < YOFFSET_MIN || yOffset > YOFFSET_MAX) throw new IllegalArgumentException(yOffset + " (range: " + YOFFSET_MIN + "-" + YOFFSET_MAX + ")");
        this.yOffset = yOffset;
    }

    public int getTickRate() { return tickRate; }
    public void setTickRate(int tickRate) {
        if (tickRate < TICKRATE_MIN || tickRate > TICKRATE_MAX) throw new IllegalArgumentException(tickRate + " (range: " + TICKRATE_MIN + "-" + TICKRATE_MAX + ")");
        this.tickRate = tickRate;
    }

    public double getShapeRadius() { return shapeRadius; }
    public void setShapeRadius(double shapeRadius) {
        if (shapeRadius < RADIUS_MIN || shapeRadius > RADIUS_MAX) throw new IllegalArgumentException(shapeRadius + " (range: " + RADIUS_MIN + "-" + RADIUS_MAX + ")");
        this.shapeRadius = shapeRadius;
    }

    public int getShapePoints() { return shapePoints; }
    public void setShapePoints(int shapePoints) {
        if (shapePoints < POINTS_MIN || shapePoints > POINTS_MAX) throw new IllegalArgumentException(shapePoints + " (range: " + POINTS_MIN + "-" + POINTS_MAX + ")");
        this.shapePoints = shapePoints;
    }

    public int getDustColorR() { return dustColorR; }
    public void setDustColorR(int dustColorR) {
        if (dustColorR < COLOR_MIN || dustColorR > COLOR_MAX) throw new IllegalArgumentException(dustColorR + " (range: " + COLOR_MIN + "-" + COLOR_MAX + ")");
        this.dustColorR = dustColorR;
    }

    public int getDustColorG() { return dustColorG; }
    public void setDustColorG(int dustColorG) {
        if (dustColorG < COLOR_MIN || dustColorG > COLOR_MAX) throw new IllegalArgumentException(dustColorG + " (range: " + COLOR_MIN + "-" + COLOR_MAX + ")");
        this.dustColorG = dustColorG;
    }

    public int getDustColorB() { return dustColorB; }
    public void setDustColorB(int dustColorB) {
        if (dustColorB < COLOR_MIN || dustColorB > COLOR_MAX) throw new IllegalArgumentException(dustColorB + " (range: " + COLOR_MIN + "-" + COLOR_MAX + ")");
        this.dustColorB = dustColorB;
    }

    public int getDustColorToR() { return dustColorToR; }
    public void setDustColorToR(int dustColorToR) {
        if (dustColorToR < COLOR_MIN || dustColorToR > COLOR_MAX) throw new IllegalArgumentException(dustColorToR + " (range: " + COLOR_MIN + "-" + COLOR_MAX + ")");
        this.dustColorToR = dustColorToR;
    }

    public int getDustColorToG() { return dustColorToG; }
    public void setDustColorToG(int dustColorToG) {
        if (dustColorToG < COLOR_MIN || dustColorToG > COLOR_MAX) throw new IllegalArgumentException(dustColorToG + " (range: " + COLOR_MIN + "-" + COLOR_MAX + ")");
        this.dustColorToG = dustColorToG;
    }

    public int getDustColorToB() { return dustColorToB; }
    public void setDustColorToB(int dustColorToB) {
        if (dustColorToB < COLOR_MIN || dustColorToB > COLOR_MAX) throw new IllegalArgumentException(dustColorToB + " (range: " + COLOR_MIN + "-" + COLOR_MAX + ")");
        this.dustColorToB = dustColorToB;
    }

    public float getDustSize() { return dustSize; }
    public void setDustSize(float dustSize) {
        if (dustSize < DUSTSIZE_MIN || dustSize > DUSTSIZE_MAX) throw new IllegalArgumentException(dustSize + " (range: " + DUSTSIZE_MIN + "-" + DUSTSIZE_MAX + ")");
        this.dustSize = dustSize;
    }
}
