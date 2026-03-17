package red.aviora.redmc.cosmetics.model;

public class ParticleLayer {

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
    public void setCount(int count) { this.count = count; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public double getOffsetX() { return offsetX; }
    public void setOffsetX(double offsetX) { this.offsetX = offsetX; }

    public double getOffsetY() { return offsetY; }
    public void setOffsetY(double offsetY) { this.offsetY = offsetY; }

    public double getOffsetZ() { return offsetZ; }
    public void setOffsetZ(double offsetZ) { this.offsetZ = offsetZ; }

    public double getYOffset() { return yOffset; }
    public void setYOffset(double yOffset) { this.yOffset = yOffset; }

    public int getTickRate() { return tickRate; }
    public void setTickRate(int tickRate) { this.tickRate = tickRate; }

    public double getShapeRadius() { return shapeRadius; }
    public void setShapeRadius(double shapeRadius) { this.shapeRadius = shapeRadius; }

    public int getShapePoints() { return shapePoints; }
    public void setShapePoints(int shapePoints) { this.shapePoints = shapePoints; }

    public int getDustColorR() { return dustColorR; }
    public void setDustColorR(int dustColorR) { this.dustColorR = dustColorR; }

    public int getDustColorG() { return dustColorG; }
    public void setDustColorG(int dustColorG) { this.dustColorG = dustColorG; }

    public int getDustColorB() { return dustColorB; }
    public void setDustColorB(int dustColorB) { this.dustColorB = dustColorB; }

    public int getDustColorToR() { return dustColorToR; }
    public void setDustColorToR(int dustColorToR) { this.dustColorToR = dustColorToR; }

    public int getDustColorToG() { return dustColorToG; }
    public void setDustColorToG(int dustColorToG) { this.dustColorToG = dustColorToG; }

    public int getDustColorToB() { return dustColorToB; }
    public void setDustColorToB(int dustColorToB) { this.dustColorToB = dustColorToB; }

    public float getDustSize() { return dustSize; }
    public void setDustSize(float dustSize) { this.dustSize = dustSize; }
}
