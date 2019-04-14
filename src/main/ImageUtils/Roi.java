//package main.ImageUtils;
//
//
//import ij.ImagePlus;
//import ij.gui.Overlay;
//import ij.macro.Interpreter;
//import ij.measure.Calibration;
//import ij.plugin.RectToolOptions;
//import ij.plugin.filter.ThresholdToSelection;
//import ij.plugin.frame.Recorder;
//import ij.process.FloatPolygon;
//import ij.process.ImageProcessor;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.StrokeType;
//
//
//import java.awt.*;
//import java.awt.event.MouseEvent;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.Enumeration;
//import java.util.Properties;
//import java.util.Vector;
//
///*
//
//@author: Jacob Zargooshifar
//
//
// */
//
//
//public class Roi implements Cloneable, Serializable {
//    public static final int CONSTRUCTING = 0;
//    public static final int MOVING = 1;
//    public static final int RESIZING = 2;
//    public static final int NORMAL = 3;
//    public static final int MOVING_HANDLE = 4;
//    public static final int RECTANGLE = 0;
//    public static final int OVAL = 1;
//    public static final int POLYGON = 2;
//    public static final int FREEROI = 3;
//    public static final int TRACED_ROI = 4;
//    public static final int LINE = 5;
//    public static final int POLYLINE = 6;
//    public static final int FREELINE = 7;
//    public static final int ANGLE = 8;
//    public static final int COMPOSITE = 9;
//    public static final int POINT = 10;
//    public static final int HANDLE_SIZE = 5;
//    public static final int NOT_PASTING = -1;
//    static final int NO_MODS = 0;
//    static final int ADD_TO_ROI = 1;
//    static final int SUBTRACT_FROM_ROI = 2;
//    int startX;
//    int startY;
//    int x;
//    int y;
//    int width;
//    int height;
//    double startXD;
//    double startYD;
//    Rectangle2D.Double bounds;
//    int activeHandle;
//    int state;
//    int modState;
//    int cornerDiameter;
//    public static ij.gui.Roi previousRoi;
//    public static final BasicStroke onePixelWide = new BasicStroke(1.0F);
//    protected static Color ROIColor;
//    protected static int pasteMode;
//    protected static int lineWidth;
//    protected static Color defaultFillColor;
//    protected int type;
//    protected int xMax;
//    protected int yMax;
//    protected ImagePlus imp;
//    private int imageID;
//    protected ImageCanvas ic;
//    protected int oldX;
//    protected int oldY;
//    protected int oldWidth;
//    protected int oldHeight;
//    protected int clipX;
//    protected int clipY;
//    protected int clipWidth;
//    protected int clipHeight;
//    protected ImagePlus clipboard;
//    protected boolean constrain;
//    protected boolean center;
//    protected boolean aspect;
//    protected boolean updateFullWindow;
//    protected double mag;
//    protected double asp_bk;
//    protected ImageProcessor cachedMask;
//    protected Color handleColor;
//    protected Color strokeColor;
//    protected Color instanceColor;
//    protected Color fillColor;
//    protected javafx.scene.shape.Shape stroke;
//    protected boolean nonScalable;
//    protected boolean overlay;
//    protected boolean wideLine;
//    protected boolean ignoreClipRect;
//    private String name;
//    private int position;
//    private int channel;
//    private int slice;
//    private int frame;
//    private Overlay prototypeOverlay;
//    private boolean subPixel;
//    private boolean activeOverlayRoi;
//    private Properties props;
//
//    public Roi(int x, int y, int width, int height) {
//        this(x, y, width, height, 0);
//    }
//
//    public Roi(double x, double y, double width, double height) {
//        this(x, y, width, height, 0);
//    }
//
//    public Roi(int x, int y, int width, int height, int cornerDiameter) {
//        this.modState = 0;
//        this.mag = 1.0D;
//        this.handleColor = Color.WHITE;
//        this.setImage((ImagePlus)null);
//        if (width < 1) {
//            width = 1;
//        }
//
//        if (height < 1) {
//            height = 1;
//        }
//
//        if (width > this.xMax) {
//            width = this.xMax;
//        }
//
//        if (height > this.yMax) {
//            height = this.yMax;
//        }
//
//        this.cornerDiameter = cornerDiameter;
//        this.x = x;
//        this.y = y;
//        this.startX = x;
//        this.startY = y;
//        this.oldX = x;
//        this.oldY = y;
//        this.oldWidth = 0;
//        this.oldHeight = 0;
//        this.width = width;
//        this.height = height;
//        this.oldWidth = width;
//        this.oldHeight = height;
//        this.clipX = x;
//        this.clipY = y;
//        this.clipWidth = width;
//        this.clipHeight = height;
//        this.state = 3;
//        this.type = 0;
//        if (this.ic != null) {
//            GraphicsContext g = this.ic.getGraphicsContext2D();
//            this.draw(g);
//
////            g.dispose();
//        }
//
//        this.fillColor = defaultFillColor;
//    }
//
//    public Roi(double x, double y, double width, double height, int cornerDiameter) {
//        this((int)x, (int)y, (int)Math.ceil(width), (int)Math.ceil(height), cornerDiameter);
//        this.bounds = new Rectangle2D.Double(x, y, width, height);
//        this.subPixel = true;
//    }
//
//    public Roi(Rectangle r) {
//        this(r.x, r.y, r.width, r.height);
//    }
//
//    public Roi(int sx, int sy, ImagePlus imp) {
//        this(sx, sy, imp, 0);
//    }
//
//    public Roi(int sx, int sy, ImagePlus imp, int cornerDiameter) {
//        this.modState = 0;
//        this.mag = 1.0D;
//        this.handleColor = Color.WHITE;
//        this.setImage(imp);
//        int ox = sx;
//        int oy = sy;
//        if (this.ic != null) {
//            ox = this.ic.offScreenX(sx);
//            oy = this.ic.offScreenY(sy);
//        }
//
//        this.setLocation(ox, oy);
//        this.cornerDiameter = cornerDiameter;
//        this.width = 0;
//        this.height = 0;
//        this.state = 0;
//        this.type = 0;
//        if (cornerDiameter > 0) {
//            double swidth = (double) RectToolOptions.getDefaultStrokeWidth();
//            if (swidth > 0.0D) {
//                this.setStrokeWidth(swidth);
//            }
//
//            Color scolor = RectToolOptions.getDefaultStrokeColor();
//            if (scolor != null) {
//                this.setStrokeColor(scolor);
//            }
//        }
//
//        this.fillColor = defaultFillColor;
//    }
//
//
//    public void setLocation(int x, int y) {
//        this.x = x;
//        this.y = y;
//        this.startX = x;
//        this.startY = y;
//        this.oldX = x;
//        this.oldY = y;
//        this.oldWidth = 0;
//        this.oldHeight = 0;
//        if (this.bounds != null) {
//            this.bounds.x = (double)x;
//            this.bounds.y = (double)y;
//        }
//
//    }
//
//    public void setLocation(double x, double y) {
//        this.setLocation((int)x, (int)y);
//        if ((double)((int)x) != x || (double)((int)y) != y) {
//            if (this.bounds != null) {
//                this.bounds.x = x;
//                this.bounds.y = y;
//            } else {
//                this.bounds = new Rectangle2D.Double(x, y, (double)this.width, (double)this.height);
//            }
//
//            this.subPixel = true;
//        }
//    }
//
//    public void setImage(ImagePlus imp) {
//        this.imp = imp;
//        this.cachedMask = null;
//        if (imp == null) {
//            this.ic = null;
//            this.clipboard = null;
//            this.xMax = this.yMax = 2147483647;
//        } else {
//            this.ic.updateImage(imp); //TODO:?? working?
//            this.xMax = imp.getWidth();
//            this.yMax = imp.getHeight();
//        }
//
//    }
//
//    public ImagePlus getImage() {
//        return this.imp;
//    }
//
//    public int getImageID() {
//        return this.imp != null ? this.imp.getID() : this.imageID;
//    }
//
//    public int getType() {
//        return this.type;
//    }
//
//    public int getState() {
//        return this.state;
//    }
//
//    public double getLength() {
//        double pw = 1.0D;
//        double ph = 1.0D;
//        if (this.imp != null) {
//            Calibration cal = this.imp.getCalibration();
//            pw = cal.pixelWidth;
//            ph = cal.pixelHeight;
//        }
//
//        return 2.0D * (double)this.width * pw + 2.0D * (double)this.height * ph;
//    }
//
//    public double getFeretsDiameter() {
//        double[] a = this.getFeretValues();
//        return a != null ? a[0] : 0.0D;
//    }
//
//    public double[] getFeretValues() {
//        double min = 1.7976931348623157E308D;
//        double diameter = 0.0D;
//        double angle = 0.0D;
//        double feretX = 0.0D;
//        double feretY = 0.0D;
//        int p1 = 0;
//        int p2 = 0;
//        double pw = 1.0D;
//        double ph = 1.0D;
//        if (this.imp != null) {
//            Calibration cal = this.imp.getCalibration();
//            pw = cal.pixelWidth;
//            ph = cal.pixelHeight;
//        }
//
//        Polygon poly = this.getConvexHull();
//        if (poly == null) {
//            poly = this.getPolygon();
//            if (poly == null) {
//                return null;
//            }
//        }
//
//        double w2 = pw * pw;
//        double h2 = ph * ph;
//
//        double dx;
//        double dy;
//        for(int i = 0; i < poly.npoints; ++i) {
//            for(int j = i; j < poly.npoints; ++j) {
//                dx = (double)(poly.xpoints[i] - poly.xpoints[j]);
//                dy = (double)(poly.ypoints[i] - poly.ypoints[j]);
//                double d = Math.sqrt(dx * dx * w2 + dy * dy * h2);
//                if (d > diameter) {
//                    diameter = d;
//                    p1 = i;
//                    p2 = j;
//                }
//            }
//        }
//
//        Rectangle r = this.getBounds();
//        double cx = (double)r.x + (double)r.width / 2.0D;
//        double cy = (double)r.y + (double)r.height / 2.0D;
//        int n = poly.npoints;
//        double[] x = new double[n];
//        double[] y = new double[n];
//
//        for(int i = 0; i < n; ++i) {
//            x[i] = ((double)poly.xpoints[i] - cx) * pw;
//            y[i] = ((double)poly.ypoints[i] - cy) * ph;
//        }
//
//        double x1;
//        double cos;
//        double sin;
//        double xmin;
//        double ymin;
//        double xmax;
//        for(x1 = 0.0D; x1 <= 90.0D; x1 += 0.5D) {
//            cos = Math.cos(x1 * 3.141592653589793D / 180.0D);
//            sin = Math.sin(x1 * 3.141592653589793D / 180.0D);
//            xmin = 1.7976931348623157E308D;
//            ymin = 1.7976931348623157E308D;
//            xmax = -1.7976931348623157E308D;
//            double ymax = -1.7976931348623157E308D;
//
//            for(int i = 0; i < n; ++i) {
//                double xr = cos * x[i] - sin * y[i];
//                double yr = sin * x[i] + cos * y[i];
//                if (xr < xmin) {
//                    xmin = xr;
//                }
//
//                if (xr > xmax) {
//                    xmax = xr;
//                }
//
//                if (yr < ymin) {
//                    ymin = yr;
//                }
//
//                if (yr > ymax) {
//                    ymax = yr;
//                }
//            }
//
//            double width = xmax - xmin;
//            double height = ymax - ymin;
//            double min2 = Math.min(width, height);
//            min = Math.min(min, min2);
//        }
//
//        x1 = (double)poly.xpoints[p1];
//        cos = (double)poly.ypoints[p1];
//        sin = (double)poly.xpoints[p2];
//        xmin = (double)poly.ypoints[p2];
//        if (x1 > sin) {
//            ymin = x1;
//            xmax = cos;
//            x1 = sin;
//            cos = xmin;
//            sin = ymin;
//            xmin = xmax;
//        }
//
//        feretX = x1 * pw;
//        feretY = cos * ph;
//        dx = sin - x1;
//        dy = cos - xmin;
//        angle = 57.29577951308232D * Math.atan2(dy * ph, dx * pw);
//        if (angle < 0.0D) {
//            angle += 180.0D;
//        }
//
//        double[] a = new double[]{diameter, angle, min, feretX, feretY};
//        return a;
//    }
//
//    public Polygon getConvexHull() {
//        return this.getPolygon();
//    }
//
//    double getFeretBreadth(Shape shape, double angle, double x1, double y1, double x2, double y2) {
//        double cx = x1 + (x2 - x1) / 2.0D;
//        double cy = y1 + (y2 - y1) / 2.0D;
//        AffineTransform at = new AffineTransform();
//        at.rotate(angle * 3.141592653589793D / 180.0D, cx, cy);
//        Shape s = at.createTransformedShape(shape);
//        Rectangle2D r = s.getBounds2D();
//        return Math.min(r.getWidth(), r.getHeight());
//    }
//
//    public Rectangle getBounds() {
//        return new Rectangle(this.x, this.y, this.width, this.height);
//    }
//
//    public Rectangle2D.Double getFloatBounds() {
//        return this.bounds != null ? new Rectangle2D.Double(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height) : new Rectangle2D.Double((double)this.x, (double)this.y, (double)this.width, (double)this.height);
//    }
//
//    /** @deprecated */
//    public Rectangle getBoundingRect() {
//        return this.getBounds();
//    }
//
//    public Polygon getPolygon() {
//        int[] xpoints = new int[4];
//        int[] ypoints = new int[4];
//        xpoints[0] = this.x;
//        ypoints[0] = this.y;
//        xpoints[1] = this.x + this.width;
//        ypoints[1] = this.y;
//        xpoints[2] = this.x + this.width;
//        ypoints[2] = this.y + this.height;
//        xpoints[3] = this.x;
//        ypoints[3] = this.y + this.height;
//        return new Polygon(xpoints, ypoints, 4);
//    }
//
//    public FloatPolygon getFloatPolygon() {
//        if (this.cornerDiameter > 0) {
//            ImageProcessor ip = this.getMask();
//            ij.gui.Roi roi2 = (new ThresholdToSelection()).convert(ip);
//            roi2.setLocation(this.x, this.y);
//            return roi2.getFloatPolygon();
//        } else if (this.subPixelResolution() && this.bounds != null) {
//            float[] xpoints = new float[4];
//            float[] ypoints = new float[4];
//            xpoints[0] = (float)this.bounds.x;
//            ypoints[0] = (float)this.bounds.y;
//            xpoints[1] = (float)(this.bounds.x + this.bounds.width);
//            ypoints[1] = (float)this.bounds.y;
//            xpoints[2] = (float)(this.bounds.x + this.bounds.width);
//            ypoints[2] = (float)(this.bounds.y + this.bounds.height);
//            xpoints[3] = (float)this.bounds.x;
//            ypoints[3] = (float)(this.bounds.y + this.bounds.height);
//            return new FloatPolygon(xpoints, ypoints);
//        } else {
//            Polygon p = this.getPolygon();
//            return new FloatPolygon(toFloat(p.xpoints), toFloat(p.ypoints), p.npoints);
//        }
//    }
//
//    public FloatPolygon getInterpolatedPolygon() {
//        return this.getInterpolatedPolygon(1.0D, false);
//    }
//
//    public FloatPolygon getInterpolatedPolygon(double interval, boolean smooth) {
//        FloatPolygon p = this instanceof Line ? ((Line)this).getFloatPoints() : this.getFloatPolygon();
//        return this.getInterpolatedPolygon(p, interval, smooth);
//    }
//
//    protected FloatPolygon getInterpolatedPolygon(FloatPolygon p, double interval, boolean smooth) {
//        boolean isLine = this.isLine();
//        double length = p.getLength(isLine);
//        int npoints2 = (int)(length * 1.5D / interval);
//        float[] xpoints2 = new float[npoints2];
//        float[] ypoints2 = new float[npoints2];
//        xpoints2[0] = p.xpoints[0];
//        ypoints2[0] = p.ypoints[0];
//        int n = 1;
//        double inc = 0.01D;
//        double distance = 0.0D;
//        double distance2 = 0.0D;
//        double dx = 0.0D;
//        double dy = 0.0D;
//        double x2 = (double)p.xpoints[0];
//        double y2 = (double)p.ypoints[0];
//        int npoints = p.npoints;
//        if (!isLine) {
//            ++npoints;
//        }
//
//        for(int i = 1; i < npoints; ++i) {
//            double x1 = x2;
//            double y1 = y2;
//            double x = x2;
//            double y = y2;
//            if (i < p.npoints) {
//                x2 = (double)p.xpoints[i];
//                y2 = (double)p.ypoints[i];
//            } else {
//                x2 = (double)p.xpoints[0];
//                y2 = (double)p.ypoints[0];
//            }
//
//            dx = x2 - x1;
//            dy = y2 - y1;
//            distance = Math.sqrt(dx * dx + dy * dy);
//            double xinc = dx * inc / distance;
//            double yinc = dy * inc / distance;
//            double lastx = (double)xpoints2[n - 1];
//            double lasty = (double)ypoints2[n - 1];
//            int n2 = (int)(distance / inc);
//            int max = xpoints2.length - 1;
//            if (npoints == 2) {
//                n2 = (int)((double)n2 + 0.5D / inc);
//                ++max;
//            }
//
//            do {
//                dx = x - lastx;
//                dy = y - lasty;
//                distance2 = Math.sqrt(dx * dx + dy * dy);
//                if (distance2 >= interval - inc / 2.0D && n < max) {
//                    xpoints2[n] = (float)x;
//                    ypoints2[n] = (float)y;
//                    ++n;
//                    lastx = x;
//                    lasty = y;
//                }
//
//                x += xinc;
//                y += yinc;
//                --n2;
//            } while(n2 > 0);
//        }
//
//        return new FloatPolygon(xpoints2, ypoints2, n);
//    }
//
//    public synchronized Object clone() {
//        try {
//            ij.gui.Roi r = (ij.gui.Roi)super.clone();
//            r.setImage((ImagePlus)null);
//            r.setStroke(this.getStroke());
//            r.setFillColor(this.getFillColor());
//            r.imageID = this.getImageID();
//            if (this.bounds != null) {
//                r.bounds = (Rectangle2D.Double)this.bounds.clone();
//            }
//
//            return r;
//        } catch (CloneNotSupportedException var2) {
//            return null;
//        }
//    }
//
//    protected void grow(int sx, int sy) {
//        if (this.clipboard == null) {
//            int xNew = this.ic.offScreenX(sx);
//            int yNew = this.ic.offScreenY(sy);
//            if (this.type == 0) {
//                if (xNew < 0) {
//                    xNew = 0;
//                }
//
//                if (yNew < 0) {
//                    yNew = 0;
//                }
//            }
//
//            if (this.constrain) {
//                if (!this.center) {
//                    this.growConstrained(xNew, yNew);
//                    return;
//                }
//
//                int dx = xNew - this.x;
//                int dy = yNew - this.y;
//                int d;
//                if (dx < dy) {
//                    d = dx;
//                } else {
//                    d = dy;
//                }
//
//                xNew = this.x + d;
//                yNew = this.y + d;
//            }
//
//            if (this.center) {
//                this.width = Math.abs(xNew - this.startX) * 2;
//                this.height = Math.abs(yNew - this.startY) * 2;
//                this.x = this.startX - this.width / 2;
//                this.y = this.startY - this.height / 2;
//            } else {
//                this.width = Math.abs(xNew - this.startX);
//                this.height = Math.abs(yNew - this.startY);
//                this.x = xNew >= this.startX ? this.startX : this.startX - this.width;
//                this.y = yNew >= this.startY ? this.startY : this.startY - this.height;
//                if (this.type == 0) {
//                    if (this.x + this.width > this.xMax) {
//                        this.width = this.xMax - this.x;
//                    }
//
//                    if (this.y + this.height > this.yMax) {
//                        this.height = this.yMax - this.y;
//                    }
//                }
//            }
//
//            this.updateClipRect();
//            this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
//            this.oldX = this.x;
//            this.oldY = this.y;
//            this.oldWidth = this.width;
//            this.oldHeight = this.height;
//            this.bounds = null;
//        }
//    }
//
//    private void growConstrained(int xNew, int yNew) {
//        int dx = xNew - this.startX;
//        int dy = yNew - this.startY;
//        this.width = this.height = (int)Math.round(Math.sqrt((double)(dx * dx + dy * dy)));
//        if (this.type == 0) {
//            this.x = xNew >= this.startX ? this.startX : this.startX - this.width;
//            this.y = yNew >= this.startY ? this.startY : this.startY - this.height;
//            if (this.x < 0) {
//                this.x = 0;
//            }
//
//            if (this.y < 0) {
//                this.y = 0;
//            }
//
//            if (this.x + this.width > this.xMax) {
//                this.width = this.xMax - this.x;
//            }
//
//            if (this.y + this.height > this.yMax) {
//                this.height = this.yMax - this.y;
//            }
//        } else {
//            this.x = this.startX + dx / 2 - this.width / 2;
//            this.y = this.startY + dy / 2 - this.height / 2;
//        }
//
//        this.updateClipRect();
//        this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
//        this.oldX = this.x;
//        this.oldY = this.y;
//        this.oldWidth = this.width;
//        this.oldHeight = this.height;
//    }
//
//    protected void moveHandle(int sx, int sy) {
//        if (this.clipboard == null) {
//            int ox = this.ic.offScreenX(sx);
//            int oy = this.ic.offScreenY(sy);
//            if (ox < 0) {
//                ox = 0;
//            }
//
//            if (oy < 0) {
//                oy = 0;
//            }
//
//            if (ox > this.xMax) {
//                ox = this.xMax;
//            }
//
//            if (oy > this.yMax) {
//                oy = this.yMax;
//            }
//
//            int x1 = this.x;
//            int y1 = this.y;
//            int x2 = x1 + this.width;
//            int y2 = this.y + this.height;
//            int xc = this.x + this.width / 2;
//            int yc = this.y + this.height / 2;
//            double asp;
//            if (this.width > 7 && this.height > 7) {
//                asp = (double)this.width / (double)this.height;
//                this.asp_bk = asp;
//            } else {
//                asp = this.asp_bk;
//            }
//
//            switch(this.activeHandle) {
//                case 0:
//                    this.x = ox;
//                    this.y = oy;
//                    break;
//                case 1:
//                    this.y = oy;
//                    break;
//                case 2:
//                    x2 = ox;
//                    this.y = oy;
//                    break;
//                case 3:
//                    x2 = ox;
//                    break;
//                case 4:
//                    x2 = ox;
//                    y2 = oy;
//                    break;
//                case 5:
//                    y2 = oy;
//                    break;
//                case 6:
//                    this.x = ox;
//                    y2 = oy;
//                    break;
//                case 7:
//                    this.x = ox;
//            }
//
//            if (this.x < x2) {
//                this.width = x2 - this.x;
//            } else {
//                this.width = 1;
//                this.x = x2;
//            }
//
//            if (this.y < y2) {
//                this.height = y2 - this.y;
//            } else {
//                this.height = 1;
//                this.y = y2;
//            }
//
//            if (this.center) {
//                switch(this.activeHandle) {
//                    case 0:
//                        this.width = (xc - this.x) * 2;
//                        this.height = (yc - this.y) * 2;
//                        break;
//                    case 1:
//                        this.height = (yc - this.y) * 2;
//                        break;
//                    case 2:
//                        this.width = (x2 - xc) * 2;
//                        this.x = x2 - this.width;
//                        this.height = (yc - this.y) * 2;
//                        break;
//                    case 3:
//                        this.width = (x2 - xc) * 2;
//                        this.x = x2 - this.width;
//                        break;
//                    case 4:
//                        this.width = (x2 - xc) * 2;
//                        this.x = x2 - this.width;
//                        this.height = (y2 - yc) * 2;
//                        this.y = y2 - this.height;
//                        break;
//                    case 5:
//                        this.height = (y2 - yc) * 2;
//                        this.y = y2 - this.height;
//                        break;
//                    case 6:
//                        this.width = (xc - this.x) * 2;
//                        this.height = (y2 - yc) * 2;
//                        this.y = y2 - this.height;
//                        break;
//                    case 7:
//                        this.width = (xc - this.x) * 2;
//                }
//
//                if (this.x >= x2) {
//                    this.width = 1;
//                    x2 = xc;
//                    this.x = xc;
//                }
//
//                if (this.y >= y2) {
//                    this.height = 1;
//                    y2 = yc;
//                    this.y = yc;
//                }
//
//                this.bounds = null;
//            }
//
//            if (this.constrain) {
//                if (this.activeHandle != 1 && this.activeHandle != 5) {
//                    this.height = this.width;
//                } else {
//                    this.width = this.height;
//                }
//
//                if (this.x >= x2) {
//                    this.width = 1;
//                    x2 = xc;
//                    this.x = xc;
//                }
//
//                if (this.y >= y2) {
//                    this.height = 1;
//                    y2 = yc;
//                    this.y = yc;
//                }
//
//                switch(this.activeHandle) {
//                    case 0:
//                        this.x = x2 - this.width;
//                        this.y = y2 - this.height;
//                        break;
//                    case 1:
//                        this.x = xc - this.width / 2;
//                        this.y = y2 - this.height;
//                        break;
//                    case 2:
//                        this.y = y2 - this.height;
//                        break;
//                    case 3:
//                        this.y = yc - this.height / 2;
//                    case 4:
//                    default:
//                        break;
//                    case 5:
//                        this.x = xc - this.width / 2;
//                        break;
//                    case 6:
//                        this.x = x2 - this.width;
//                        break;
//                    case 7:
//                        this.y = yc - this.height / 2;
//                        this.x = x2 - this.width;
//                }
//
//                if (this.center) {
//                    this.x = xc - this.width / 2;
//                    this.y = yc - this.height / 2;
//                }
//            }
//
//            if (this.aspect && !this.constrain) {
//                if (this.activeHandle != 1 && this.activeHandle != 5) {
//                    this.height = (int)Math.rint((double)this.width / asp);
//                } else {
//                    this.width = (int)Math.rint((double)this.height * asp);
//                }
//
//                switch(this.activeHandle) {
//                    case 0:
//                        this.x = x2 - this.width;
//                        this.y = y2 - this.height;
//                        break;
//                    case 1:
//                        this.x = xc - this.width / 2;
//                        this.y = y2 - this.height;
//                        break;
//                    case 2:
//                        this.y = y2 - this.height;
//                        break;
//                    case 3:
//                        this.y = yc - this.height / 2;
//                    case 4:
//                    default:
//                        break;
//                    case 5:
//                        this.x = xc - this.width / 2;
//                        break;
//                    case 6:
//                        this.x = x2 - this.width;
//                        break;
//                    case 7:
//                        this.y = yc - this.height / 2;
//                        this.x = x2 - this.width;
//                }
//
//                if (this.center) {
//                    this.x = xc - this.width / 2;
//                    this.y = yc - this.height / 2;
//                }
//
//                if (this.width < 8) {
//                    if (this.width < 1) {
//                        this.width = 1;
//                    }
//
//                    this.height = (int)Math.rint((double)this.width / this.asp_bk);
//                }
//
//                if (this.height < 8) {
//                    if (this.height < 1) {
//                        this.height = 1;
//                    }
//
//                    this.width = (int)Math.rint((double)this.height * this.asp_bk);
//                }
//            }
//
//            this.updateClipRect();
//            this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
//            this.oldX = this.x;
//            this.oldY = this.y;
//            this.oldWidth = this.width;
//            this.oldHeight = this.height;
//            this.bounds = null;
//            this.subPixel = false;
//        }
//    }
//
//    void move(int sx, int sy) {
//        int xNew = this.ic.offScreenX(sx);
//        int yNew = this.ic.offScreenY(sy);
//        int dx = xNew - this.startX;
//        int dy = yNew - this.startY;
//        if (dx != 0 || dy != 0) {
//            this.x += dx;
//            this.y += dy;
//            if (this.bounds != null) {
//                Rectangle2D.Double var10000 = this.bounds;
//                var10000.x += (double)dx;
//                var10000 = this.bounds;
//                var10000.y += (double)dy;
//            }
//
//            boolean isImageRoi = this instanceof ImageRoi;
//            if (this.clipboard == null && this.type == 0 && !isImageRoi) {
//                if (this.x < 0) {
//                    this.x = 0;
//                }
//
//                if (this.y < 0) {
//                    this.y = 0;
//                }
//
//                if (this.x + this.width > this.xMax) {
//                    this.x = this.xMax - this.width;
//                }
//
//                if (this.y + this.height > this.yMax) {
//                    this.y = this.yMax - this.height;
//                }
//            }
//
//            this.startX = xNew;
//            this.startY = yNew;
//            if (this instanceof TextRoi && ((TextRoi)this).getAngle() != 0.0D) {
//                this.ignoreClipRect = true;
//            }
//
//            this.updateClipRect();
//            if ((lineWidth <= 1 || !this.isLine()) && !this.ignoreClipRect && (!(this instanceof PolygonRoi) || !((PolygonRoi)this).isSplineFit())) {
//                this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
//            } else {
//                this.imp.draw();
//            }
//
//            this.oldX = this.x;
//            this.oldY = this.y;
//            this.oldWidth = this.width;
//            this.oldHeight = this.height;
//            if (isImageRoi) {
//                this.showStatus();
//            }
//
//            if (this.bounds != null) {
//                this.bounds.x = (double)this.x;
//                this.bounds.y = (double)this.y;
//            }
//
//        }
//    }
//
//    public void nudge(int key) {
//        switch(key) {
//            case 37:
//                --this.x;
//                if (this.x < 0 && (this.type != 0 || this.clipboard == null)) {
//                    this.x = 0;
//                }
//                break;
//            case 38:
//                --this.y;
//                if (this.y < 0 && (this.type != 0 || this.clipboard == null)) {
//                    this.y = 0;
//                }
//                break;
//            case 39:
//                ++this.x;
//                if (this.x + this.width >= this.xMax && (this.type != 0 || this.clipboard == null)) {
//                    this.x = this.xMax - this.width;
//                }
//                break;
//            case 40:
//                ++this.y;
//                if (this.y + this.height >= this.yMax && (this.type != 0 || this.clipboard == null)) {
//                    this.y = this.yMax - this.height;
//                }
//        }
//
//        this.updateClipRect();
//        this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
//        this.oldX = this.x;
//        this.oldY = this.y;
//        this.bounds = null;
//        this.showStatus();
//    }
//
//    public void nudgeCorner(int key) {
//        if (this.type <= 1 && this.clipboard == null) {
//            switch(key) {
//                case 37:
//                    --this.width;
//                    if (this.width < 1) {
//                        this.width = 1;
//                    }
//                    break;
//                case 38:
//                    --this.height;
//                    if (this.height < 1) {
//                        this.height = 1;
//                    }
//                    break;
//                case 39:
//                    ++this.width;
//                    if (this.x + this.width > this.xMax) {
//                        this.width = this.xMax - this.x;
//                    }
//                    break;
//                case 40:
//                    ++this.height;
//                    if (this.y + this.height > this.yMax) {
//                        this.height = this.yMax - this.y;
//                    }
//            }
//
//            this.updateClipRect();
//            this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
//            this.oldX = this.x;
//            this.oldY = this.y;
//            this.cachedMask = null;
//            this.showStatus();
//        }
//    }
//
//    protected void updateClipRect() {
//        this.clipX = this.x <= this.oldX ? this.x : this.oldX;
//        this.clipY = this.y <= this.oldY ? this.y : this.oldY;
//        this.clipWidth = (this.x + this.width >= this.oldX + this.oldWidth ? this.x + this.width : this.oldX + this.oldWidth) - this.clipX + 1;
//        this.clipHeight = (this.y + this.height >= this.oldY + this.oldHeight ? this.y + this.height : this.oldY + this.oldHeight) - this.clipY + 1;
//        int m = 3;
//        if (this.ic != null) {
//            double mag = this.ic.getMagnification();
//            if (mag < 1.0D) {
//                m = (int)(4.0D / mag);
//            }
//        }
//
//        m += this.clipRectMargin();
//        m = (int)((float)m + this.getStrokeWidth() * 2.0F);
//        this.clipX -= m;
//        this.clipY -= m;
//        this.clipWidth += m * 2;
//        this.clipHeight += m * 2;
//    }
//
//    protected int clipRectMargin() {
//        return 0;
//    }
//
//    protected void handleMouseDrag(int sx, int sy, int flags) {
//        if (this.ic != null) {
//            this.constrain = (flags & 1) != 0;
//            this.center = (flags & 2) != 0 || IJ.isMacintosh() && (flags & 4) != 0;
//            this.aspect = (flags & 8) != 0;
//            switch(this.state) {
//                case 0:
//                    this.grow(sx, sy);
//                    break;
//                case 1:
//                    this.move(sx, sy);
//                case 2:
//                case 3:
//                default:
//                    break;
//                case 4:
//                    this.moveHandle(sx, sy);
//            }
//
//        }
//    }
//
//    int getHandleSize() {
//        double mag = this.ic != null ? this.ic.getMagnification() : 1.0D;
//        double size = 5.0D / mag;
//        return (int)(size * mag);
//    }
//
//    public void draw(GraphicsContext g) {
//        Color color = this.strokeColor != null ? this.strokeColor : ROIColor;
//        if (this.fillColor != null) {
//            color = this.fillColor;
//        }
//
//        if (!Interpreter.isBatchMode() || this.imp == null || this.imp.getOverlay() == null || this.strokeColor != null || this.fillColor != null) {
//
//            g.setFill(color);
//            this.mag = this.getMagnification();
//            int sw = (int)((double)this.width * this.mag);
//            int sh = (int)((double)this.height * this.mag);
//            int sx1 = this.screenX(this.x);
//            int sy1 = this.screenY(this.y);
//            if (this.subPixelResolution() && this.bounds != null) {
//                sw = (int)(this.bounds.width * this.mag);
//                sh = (int)(this.bounds.height * this.mag);
//                sx1 = this.screenXD(this.bounds.x);
//                sy1 = this.screenYD(this.bounds.y);
//            }
//
//            int sx2 = sx1 + sw / 2;
//            int sy2 = sy1 + sh / 2;
//            int sx3 = sx1 + sw;
//            int sy3 = sy1 + sh;
//            GraphicsContext g2d = g;
//            if (this.stroke != null) {
//                g2d.setStroke(this.getScaledStroke());
//            }
//
//            if (this.cornerDiameter > 0) {
//                int sArcSize = (int)Math.round((double)this.cornerDiameter * this.mag);
//                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                if (this.fillColor != null) {
//                    g.fillRoundRect(sx1, sy1, sw, sh, sArcSize, sArcSize);
//                } else {
//                    g.drawRoundRect(sx1, sy1, sw, sh, sArcSize, sArcSize);
//                }
//            } else if (this.fillColor != null) {
//                if (!this.overlay && this.isActiveOverlayRoi()) {
//                    g.setColor(Color.cyan);
//                    g.drawRect(sx1, sy1, sw, sh);
//                } else if (!(this instanceof TextRoi)) {
//                    g.fillRect(sx1, sy1, sw, sh);
//                } else {
//                    g.drawRect(sx1, sy1, sw, sh);
//                }
//            } else {
//                g.drawRect(sx1, sy1, sw, sh);
//            }
//
//            if (this.state != 0 && this.clipboard == null && !this.overlay) {
//                int size2 = 2;
//                this.drawHandle(g, sx1 - size2, sy1 - size2);
//                this.drawHandle(g, sx2 - size2, sy1 - size2);
//                this.drawHandle(g, sx3 - size2, sy1 - size2);
//                this.drawHandle(g, sx3 - size2, sy2 - size2);
//                this.drawHandle(g, sx3 - size2, sy3 - size2);
//                this.drawHandle(g, sx2 - size2, sy3 - size2);
//                this.drawHandle(g, sx1 - size2, sy3 - size2);
//                this.drawHandle(g, sx1 - size2, sy2 - size2);
//            }
//
//            this.drawPreviousRoi(g);
//            if (this.state != 3) {
//                this.showStatus();
//            }
//
//            if (this.updateFullWindow) {
//                this.updateFullWindow = false;
//                this.imp.draw();
//            }
//
//        }
//    }
//
//    public void drawOverlay(Graphics g) {
//        this.overlay = true;
//        this.draw(g);
//        this.overlay = false;
//    }
//
//    void drawPreviousRoi(Graphics g) {
//        if (previousRoi != null && previousRoi != this && previousRoi.modState != 0) {
//            if (this.type != 10 && previousRoi.getType() == 10 && previousRoi.modState != 2) {
//                return;
//            }
//
//            previousRoi.setImage(this.imp);
//            previousRoi.draw(g);
//        }
//
//    }
//
//    void drawHandle(Graphics g, int x, int y) {
//        double size = (double)(this.width * this.height) * this.mag * this.mag;
//        if (this.type == 5) {
//            size = Math.sqrt((double)(this.width * this.width + this.height * this.height));
//            size *= size * this.mag * this.mag;
//        }
//
//        if (size > 4000.0D) {
//            g.setColor(Color.black);
//            g.fillRect(x, y, 5, 5);
//            g.setColor(this.handleColor);
//            g.fillRect(x + 1, y + 1, 3, 3);
//        } else if (size > 1000.0D) {
//            g.setColor(Color.black);
//            g.fillRect(x + 1, y + 1, 4, 4);
//            g.setColor(this.handleColor);
//            g.fillRect(x + 2, y + 2, 2, 2);
//        } else {
//            g.setColor(Color.black);
//            g.fillRect(x + 1, y + 1, 3, 3);
//            g.setColor(this.handleColor);
//            g.fillRect(x + 2, y + 2, 1, 1);
//        }
//
//    }
//
//    /** @deprecated */
//    public void drawPixels() {
//        if (this.imp != null) {
//            this.drawPixels(this.imp.getProcessor());
//        }
//
//    }
//
//    public void drawPixels(ImageProcessor ip) {
//        this.endPaste();
//        int saveWidth = ip.getLineWidth();
//        if (this.getStrokeWidth() > 1.0F) {
//            ip.setLineWidth(Math.round(this.getStrokeWidth()));
//        }
//
//        if (this.cornerDiameter > 0) {
//            (new ShapeRoi(new RoundRectangle2D.Float((float)this.x, (float)this.y, (float)this.width, (float)this.height, (float)this.cornerDiameter, (float)this.cornerDiameter))).drawPixels(ip);
//        } else if (ip.getLineWidth() == 1) {
//            ip.drawRect(this.x, this.y, this.width + 1, this.height + 1);
//        } else {
//            ip.drawRect(this.x, this.y, this.width, this.height);
//        }
//
//        ip.setLineWidth(saveWidth);
//        if (Line.getWidth() > 1 || this.getStrokeWidth() > 1.0F) {
//            this.updateFullWindow = true;
//        }
//
//    }
//
//    public boolean contains(int x, int y) {
//        Rectangle r = new Rectangle(this.x, this.y, this.width, this.height);
//        boolean contains = r.contains(x, y);
//        if (this.cornerDiameter != 0 && contains) {
//            RoundRectangle2D rr = new RoundRectangle2D.Float((float)this.x, (float)this.y, (float)this.width, (float)this.height, (float)this.cornerDiameter, (float)this.cornerDiameter);
//            return rr.contains((double)x, (double)y);
//        } else {
//            return contains;
//        }
//    }
//
//    public int isHandle(int sx, int sy) {
//        if (this.clipboard == null && this.ic != null) {
//            double mag = this.ic.getMagnification();
//            int size = 8;
//            int halfSize = size / 2;
//            double x = this.getXBase();
//            double y = this.getYBase();
//            double width = this.getFloatWidth();
//            double height = this.getFloatHeight();
//            int sx1 = this.ic.screenXD(x) - halfSize;
//            int sy1 = this.ic.screenYD(y) - halfSize;
//            int sx3 = this.ic.screenXD(x + width) - halfSize;
//            int sy3 = this.ic.screenYD(y + height) - halfSize;
//            int sx2 = sx1 + (sx3 - sx1) / 2;
//            int sy2 = sy1 + (sy3 - sy1) / 2;
//            if (sx >= sx1 && sx <= sx1 + size && sy >= sy1 && sy <= sy1 + size) {
//                return 0;
//            } else if (sx >= sx2 && sx <= sx2 + size && sy >= sy1 && sy <= sy1 + size) {
//                return 1;
//            } else if (sx >= sx3 && sx <= sx3 + size && sy >= sy1 && sy <= sy1 + size) {
//                return 2;
//            } else if (sx >= sx3 && sx <= sx3 + size && sy >= sy2 && sy <= sy2 + size) {
//                return 3;
//            } else if (sx >= sx3 && sx <= sx3 + size && sy >= sy3 && sy <= sy3 + size) {
//                return 4;
//            } else if (sx >= sx2 && sx <= sx2 + size && sy >= sy3 && sy <= sy3 + size) {
//                return 5;
//            } else if (sx >= sx1 && sx <= sx1 + size && sy >= sy3 && sy <= sy3 + size) {
//                return 6;
//            } else {
//                return sx >= sx1 && sx <= sx1 + size && sy >= sy2 && sy <= sy2 + size ? 7 : -1;
//            }
//        } else {
//            return -1;
//        }
//    }
//
//    protected void mouseDownInHandle(int handle, int sx, int sy) {
//        this.state = 4;
//        this.activeHandle = handle;
//    }
//
//    protected void handleMouseDown(int sx, int sy) {
//        if (this.state == 3 && this.ic != null) {
//            this.state = 1;
//            this.startX = this.ic.offScreenX(sx);
//            this.startY = this.ic.offScreenY(sy);
//            this.startXD = this.ic.offScreenXD(sx);
//            this.startYD = this.ic.offScreenYD(sy);
//        }
//
//    }
//
//    protected void handleMouseUp(int screenX, int screenY) {
//        this.state = 3;
//        if (this.imp != null) {
//            this.imp.draw(this.clipX - 5, this.clipY - 5, this.clipWidth + 10, this.clipHeight + 10);
//            if (Recorder.record) {
//                if (this.type == 1) {
//                    Recorder.record("makeOval", this.x, this.y, this.width, this.height);
//                } else if (!(this instanceof TextRoi)) {
//                    if (this.cornerDiameter == 0) {
//                        Recorder.record("makeRectangle", this.x, this.y, this.width, this.height);
//                    } else if (Recorder.scriptMode()) {
//                        Recorder.recordCall("imp.setRoi(new Roi(" + this.x + ", " + this.y + ", " + this.width + ", " + this.height + ", " + this.cornerDiameter + "));");
//                    } else {
//                        Recorder.record("makeRectangle", this.x, this.y, this.width, this.height, this.cornerDiameter);
//                    }
//                }
//            }
//
//            if (Toolbar.getToolId() == 1 && Toolbar.getBrushSize() > 0) {
//                int flags = this.ic != null ? this.ic.getModifiers() : 16;
//                if ((flags & 16) == 0) {
//                    this.imp.draw();
//                    return;
//                }
//            }
//
//            this.modifyRoi();
//        }
//    }
//
//    void modifyRoi() {
//        if (previousRoi != null && previousRoi.modState != 0 && this.imp != null) {
//            if (this.type != 10 && previousRoi.getType() != 10) {
//                ij.gui.Roi previous = (ij.gui.Roi)previousRoi.clone();
//                previous.modState = 0;
//                ShapeRoi s1 = null;
//                ShapeRoi s2 = null;
//                if (previousRoi instanceof ShapeRoi) {
//                    s1 = (ShapeRoi)previousRoi;
//                } else {
//                    s1 = new ShapeRoi(previousRoi);
//                }
//
//                if (this instanceof ShapeRoi) {
//                    s2 = (ShapeRoi)this;
//                } else {
//                    s2 = new ShapeRoi(this);
//                }
//
//                if (previousRoi.modState == 1) {
//                    s1.or(s2);
//                } else {
//                    s1.not(s2);
//                }
//
//                previousRoi.modState = 0;
//                ij.gui.Roi[] rois = s1.getRois();
//                if (rois.length != 0) {
//                    int type2 = rois[0].getType();
//                    ij.gui.Roi roi2 = null;
//                    if (rois.length != 1 || type2 != 2 && type2 != 3) {
//                        roi2 = s1;
//                    } else {
//                        roi2 = rois[0];
//                    }
//
//                    if (roi2 != null) {
//                        ((ij.gui.Roi)roi2).copyAttributes(previousRoi);
//                    }
//
//                    this.imp.setRoi((ij.gui.Roi)roi2);
//                    previousRoi = previous;
//                }
//            } else {
//                if (this.type == 10 && previousRoi.getType() == 10) {
//                    this.addPoint();
//                } else if (this.isArea() && previousRoi.getType() == 10 && previousRoi.modState == 2) {
//                    this.subtractPoints();
//                }
//
//            }
//        }
//    }
//
//    void addPoint() {
//        if (this.type == 10 && previousRoi.getType() == 10) {
//            previousRoi.modState = 0;
//            PointRoi p1 = (PointRoi)previousRoi;
//            Rectangle r = this.getBounds();
//            FloatPolygon poly = this.getFloatPolygon();
//            this.imp.setRoi(p1.addPoint((double)poly.xpoints[0], (double)poly.ypoints[0]));
//        } else {
//            this.modState = 0;
//            this.imp.draw();
//        }
//    }
//
//    void subtractPoints() {
//        previousRoi.modState = 0;
//        PointRoi p1 = (PointRoi)previousRoi;
//        PointRoi p2 = p1.subtractPoints(this);
//        if (p2 != null) {
//            this.imp.setRoi(p1.subtractPoints(this));
//        } else {
//            this.imp.deleteRoi();
//        }
//
//    }
//
//    public void update(boolean add, boolean subtract) {
//        if (previousRoi != null) {
//            if (add) {
//                previousRoi.modState = 1;
//                this.modifyRoi();
//            } else if (subtract) {
//                previousRoi.modState = 2;
//                this.modifyRoi();
//            } else {
//                previousRoi.modState = 0;
//            }
//
//        }
//    }
//
//    protected void showStatus() {
//        if (this.imp != null) {
//            String value;
//            if (this.state != 0 && (this.type == 0 || this.type == 10) && this.width <= 25 && this.height <= 25) {
//                ImageProcessor ip = this.imp.getProcessor();
//                double v = (double)ip.getPixelValue(this.x, this.y);
//                int digits = this.imp.getType() != 0 && this.imp.getType() != 1 ? 2 : 0;
//                value = ", value=" + IJ.d2s(v, digits);
//            } else {
//                value = "";
//            }
//
//            Calibration cal = this.imp.getCalibration();
//            String size;
//            if (cal.scaled() && !IJ.altKeyDown()) {
//                size = ", w=" + IJ.d2s((double)this.width * cal.pixelWidth) + ", h=" + IJ.d2s((double)this.height * cal.pixelHeight);
//            } else {
//                size = ", w=" + this.width + ", h=" + this.height;
//            }
//
//            IJ.showStatus(this.imp.getLocationAsString(this.x, this.y) + size + value);
//        }
//    }
//
//    public ImageProcessor getMask() {
//        return this.cornerDiameter > 0 ? (new ShapeRoi(new RoundRectangle2D.Float((float)this.x, (float)this.y, (float)this.width, (float)this.height, (float)this.cornerDiameter, (float)this.cornerDiameter))).getMask() : null;
//    }
//
//    public void startPaste(ImagePlus clipboard) {
//        IJ.showStatus("Pasting...");
//        this.clipboard = clipboard;
//        this.imp.getProcessor().snapshot();
//        this.updateClipRect();
//        this.imp.draw(this.clipX, this.clipY, this.clipWidth, this.clipHeight);
//    }
//
//    void updatePaste() {
//        if (this.clipboard != null) {
//            this.imp.getMask();
//            ImageProcessor ip = this.imp.getProcessor();
//            ip.reset();
//            int xoffset = 0;
//            int yoffset = 0;
//            ij.gui.Roi croi = this.clipboard.getRoi();
//            if (croi != null) {
//                Rectangle r = croi.getBounds();
//                if (r.x < 0) {
//                    xoffset = -r.x;
//                }
//
//                if (r.y < 0) {
//                    yoffset = -r.y;
//                }
//            }
//
//            ip.copyBits(this.clipboard.getProcessor(), this.x + xoffset, this.y + yoffset, pasteMode);
//            if (this.type != 0) {
//                ip.reset(ip.getMask());
//            }
//
//            if (this.ic != null) {
//                this.ic.setImageUpdated();
//            }
//        }
//
//    }
//
//    public void endPaste() {
//        if (this.clipboard != null) {
//            this.updatePaste();
//            this.clipboard = null;
//            Undo.setup(1, this.imp);
//        }
//
//        this.activeOverlayRoi = false;
//    }
//
//    public void abortPaste() {
//        this.clipboard = null;
//        this.imp.getProcessor().reset();
//        this.imp.updateAndDraw();
//    }
//
//    public double getAngle(int x1, int y1, int x2, int y2) {
//        return this.getFloatAngle((double)x1, (double)y1, (double)x2, (double)y2);
//    }
//
//    public double getFloatAngle(double x1, double y1, double x2, double y2) {
//        double dx = x2 - x1;
//        double dy = y1 - y2;
//        if (this.imp != null && !IJ.altKeyDown()) {
//            Calibration cal = this.imp.getCalibration();
//            dx *= cal.pixelWidth;
//            dy *= cal.pixelHeight;
//        }
//
//        return 57.29577951308232D * Math.atan2(dy, dx);
//    }
//
//    public static void setColor(Color c) {
//        ROIColor = c;
//    }
//
//    public static Color getColor() {
//        return ROIColor;
//    }
//
//    public void setStrokeColor(Color c) {
//        this.strokeColor = c;
//    }
//
//    public Color getStrokeColor() {
//        return this.strokeColor;
//    }
//
//    public void setFillColor(Color color) {
//        this.fillColor = color;
//    }
//
//    public Color getFillColor() {
//        return this.fillColor;
//    }
//
//    public static void setDefaultFillColor(Color color) {
//        defaultFillColor = color;
//    }
//
//    public static Color getDefaultFillColor() {
//        return defaultFillColor;
//    }
//
//    public void copyAttributes(ij.gui.Roi roi2) {
//        this.strokeColor = roi2.strokeColor;
//        this.fillColor = roi2.fillColor;
//        this.stroke = roi2.stroke;
//    }
//
//    /** @deprecated */
//    public void setInstanceColor(Color c) {
//        this.strokeColor = c;
//    }
//
//    /** @deprecated */
//    public void setLineWidth(int width) {
//        this.setStrokeWidth((float)width);
//    }
//
//    public void updateWideLine(float width) {
//        if (this.isLine()) {
//            this.wideLine = true;
//            this.setStrokeWidth(width);
//            if (this.getStrokeColor() == null) {
//                Color c = getColor();
//                this.setStrokeColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 77));
//            }
//        }
//
//    }
//
//    public void setNonScalable(boolean nonScalable) {
//        this.nonScalable = nonScalable;
//    }
//
//    public void setStrokeWidth(float width) {
//        if (width < 0.0F) {
//            width = 0.0F;
//        }
//
//        if (width == 0.0F) {
//            this.stroke = null;
//        } else if (this.wideLine) {
//            javafx.scene.shape.Shape shape = new javafx.scene.shape.Rectangle();
//            shape.setStroke(fillColor);
//            shape.setStrokeWidth(width);
//            shape.setStrokeType(StrokeType.CENTERED);
//
//            this.stroke = shape;
//        } else {
//            javafx.scene.shape.Shape shape = new javafx.scene.shape.Rectangle();
//            shape.setStroke(fillColor);
//            shape.setStrokeWidth(width);
//            shape.setStrokeType(StrokeType.CENTERED);
//
//            this.stroke = shape;
//        }
//
//        if (width > 1.0F) {
//            this.fillColor = null;
//        }
//
//    }
//
//    public void setStrokeWidth(double width) {
//        this.setStrokeWidth((float)width);
//    }
//
//    public double getStrokeWidth() {
//        return this.stroke != null ? this.stroke.getStrokeWidth() : 0.0F;
//    }
//
//    public void setStroke(javafx.scene.shape.Shape stroke) {
//        this.stroke = stroke;
//    }
//
//    public javafx.scene.shape.Shape getStroke() {
//        return this.stroke;
//    }
//
//
//    protected javafx.scene.shape.Shape getScaledStroke() {
//
//        if (this.ic == null) {
//            return this.stroke;
//        } else {
//            double mag = this.ic.getMagnification();
//            if (mag != 1.0D) {
//                float width = (float)((double)this.stroke.getStrokeWidth() * mag);
//                javafx.scene.shape.Shape shape = new javafx.scene.shape.Rectangle();
//                shape.setStroke(fillColor);shape.setStrokeWidth(10);shape.setStrokeType(StrokeType.CENTERED);
//                return shape;
//            } else {
//                return this.stroke;
//            }
//        }
//    }
//
//    public String getName() {
//        return this.name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public static void setPasteMode(int transferMode) {
//        if (transferMode != pasteMode) {
//            pasteMode = transferMode;
//            ImagePlus imp = WindowManager.getCurrentImage();
//            if (imp != null) {
//                imp.updateAndDraw();
//            }
//
//        }
//    }
//
//    public void setCornerDiameter(int cornerDiameter) {
//        if (cornerDiameter < 0) {
//            cornerDiameter = 0;
//        }
//
//        this.cornerDiameter = cornerDiameter;
//        ImagePlus imp = WindowManager.getCurrentImage();
//        if (imp != null && this == imp.getRoi()) {
//            imp.updateAndDraw();
//        }
//
//    }
//
//    public int getCornerDiameter() {
//        return this.cornerDiameter;
//    }
//
//    public void setRoundRectArcSize(int cornerDiameter) {
//        this.setCornerDiameter(cornerDiameter);
//    }
//
//    public int getRoundRectArcSize() {
//        return this.cornerDiameter;
//    }
//
//    public void setPosition(int n) {
//        if (n < 0) {
//            n = 0;
//        }
//
//        this.position = n;
//        this.channel = this.slice = this.frame = 0;
//    }
//
//    public int getPosition() {
//        return this.position;
//    }
//
//    public void setPosition(int channel, int slice, int frame) {
//        if (channel < 0) {
//            channel = 0;
//        }
//
//        this.channel = channel;
//        if (slice < 0) {
//            slice = 0;
//        }
//
//        this.slice = slice;
//        if (frame < 0) {
//            frame = 0;
//        }
//
//        this.frame = frame;
//        this.position = 0;
//    }
//
//    public final int getCPosition() {
//        return this.channel;
//    }
//
//    public final int getZPosition() {
//        return this.slice;
//    }
//
//    public final int getTPosition() {
//        return this.frame;
//    }
//
//    public void setPrototypeOverlay(Overlay overlay) {
//        this.prototypeOverlay = new Overlay();
//        this.prototypeOverlay.drawLabels(overlay.getDrawLabels());
//        this.prototypeOverlay.drawNames(overlay.getDrawNames());
//        this.prototypeOverlay.drawBackgrounds(overlay.getDrawBackgrounds());
//        this.prototypeOverlay.setLabelColor(overlay.getLabelColor());
//        this.prototypeOverlay.setLabelFont(overlay.getLabelFont());
//    }
//
//    public Overlay getPrototypeOverlay() {
//        return this.prototypeOverlay != null ? this.prototypeOverlay : new Overlay();
//    }
//
//    public int getPasteMode() {
//        return this.clipboard == null ? -1 : pasteMode;
//    }
//
//    public static int getCurrentPasteMode() {
//        return pasteMode;
//    }
//
//    public boolean isArea() {
//        return this.type >= 0 && this.type <= 4 || this.type == 9;
//    }
//
//    public boolean isLine() {
//        return this.type >= 5 && this.type <= 7;
//    }
//
//    public boolean isDrawingTool() {
//        return false;
//    }
//
//    protected double getMagnification() {
//        return this.ic != null ? this.ic.getMagnification() : 1.0D;
//    }
//
//    public String getTypeAsString() {
//        String s = "";
//        switch(this.type) {
//            case 1:
//                s = "Oval";
//                break;
//            case 2:
//                s = "Polygon";
//                break;
//            case 3:
//                s = "Freehand";
//                break;
//            case 4:
//                s = "Traced";
//                break;
//            case 5:
//                s = "Straight Line";
//                break;
//            case 6:
//                s = "Polyline";
//                break;
//            case 7:
//                s = "Freeline";
//                break;
//            case 8:
//                s = "Angle";
//                break;
//            case 9:
//                s = "Composite";
//                break;
//            case 10:
//                s = "Point";
//                break;
//            default:
//                s = this instanceof TextRoi ? "Text" : "Rectangle";
//        }
//
//        return s;
//    }
//
//    public boolean isVisible() {
//        return this.ic != null;
//    }
//
//    public boolean subPixelResolution() {
//        return this.subPixel;
//    }
//
//    public boolean getDrawOffset() {
//        return false;
//    }
//
//    public void setDrawOffset(boolean drawOffset) {
//    }
//
//    public void setIgnoreClipRect(boolean ignoreClipRect) {
//        this.ignoreClipRect = ignoreClipRect;
//    }
//
//    public final boolean isActiveOverlayRoi() {
//        if (this.imp == null) {
//            return false;
//        } else {
//            Overlay overlay = this.imp.getOverlay();
//            if (overlay != null && overlay.contains(this)) {
//                return true;
//            } else {
//                ImageCanvas ic = this.imp.getCanvas();
//                overlay = ic != null ? ic.getShowAllList() : null;
//                return overlay != null && overlay.contains(this);
//            }
//        }
//    }
//
//    public boolean equals(Object obj) {
//        if (obj instanceof ij.gui.Roi) {
//            ij.gui.Roi roi2 = (ij.gui.Roi)obj;
//            if (this.type != roi2.getType()) {
//                return false;
//            } else if (!this.getBounds().equals(roi2.getBounds())) {
//                return false;
//            } else {
//                return this.getLength() == roi2.getLength();
//            }
//        } else {
//            return false;
//        }
//    }
//
//    protected int screenX(int ox) {
//        return this.ic != null ? this.ic.screenX(ox) : ox;
//    }
//
//    protected int screenY(int oy) {
//        return this.ic != null ? this.ic.screenY(oy) : oy;
//    }
//
//    protected int screenXD(double ox) {
//        return this.ic != null ? this.ic.screenXD(ox) : (int)ox;
//    }
//
//    protected int screenYD(double oy) {
//        return this.ic != null ? this.ic.screenYD(oy) : (int)oy;
//    }
//
//    public static int[] toInt(float[] arr) {
//        return toInt(arr, (int[])null, arr.length);
//    }
//
//    public static int[] toInt(float[] arr, int[] arr2, int size) {
//        int n = arr.length;
//        if (size > n) {
//            size = n;
//        }
//
//        int[] temp = arr2;
//        if (arr2 == null || arr2.length < n) {
//            temp = new int[n];
//        }
//
//        for(int i = 0; i < size; ++i) {
//            temp[i] = (int)arr[i];
//        }
//
//        return temp;
//    }
//
//    public static int[] toIntR(float[] arr) {
//        int n = arr.length;
//        int[] temp = new int[n];
//
//        for(int i = 0; i < n; ++i) {
//            temp[i] = (int)Math.floor((double)arr[i] + 0.5D);
//        }
//
//        return temp;
//    }
//
//    public static float[] toFloat(int[] arr) {
//        int n = arr.length;
//        float[] temp = new float[n];
//
//        for(int i = 0; i < n; ++i) {
//            temp[i] = (float)arr[i];
//        }
//
//        return temp;
//    }
//
//    public void setProperty(String key, String value) {
//        if (key != null) {
//            if (this.props == null) {
//                this.props = new Properties();
//            }
//
//            if (value != null && value.length() != 0) {
//                this.props.setProperty(key, value);
//            } else {
//                this.props.remove(key);
//            }
//
//        }
//    }
//
//    public String getProperty(String property) {
//        return this.props == null ? null : this.props.getProperty(property);
//    }
//
//    public void setProperties(String properties) {
//        if (this.props == null) {
//            this.props = new Properties();
//        } else {
//            this.props.clear();
//        }
//
//        try {
//            InputStream is = new ByteArrayInputStream(properties.getBytes("utf-8"));
//            this.props.load(is);
//        } catch (Exception var3) {
//            IJ.error("" + var3);
//        }
//
//    }
//
//    public String getProperties() {
//        if (this.props == null) {
//            return null;
//        } else {
//            Vector v = new Vector();
//            Enumeration en = this.props.keys();
//
//            while(en.hasMoreElements()) {
//                v.addElement(en.nextElement());
//            }
//
//            String[] keys = new String[v.size()];
//
//            for(int i = 0; i < keys.length; ++i) {
//                keys[i] = (String)v.elementAt(i);
//            }
//
//            Arrays.sort(keys);
//            StringBuffer sb = new StringBuffer();
//
//            for(int i = 0; i < keys.length; ++i) {
//                sb.append(keys[i]);
//                sb.append(": ");
//                sb.append(this.props.get(keys[i]));
//                sb.append("\n");
//            }
//
//            return sb.toString();
//        }
//    }
//
//    public int getPropertyCount() {
//        return this.props == null ? 0 : this.props.size();
//    }
//
//    public String toString() {
//        return "Roi[" + this.getTypeAsString() + ", x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + "]";
//    }
//
//    public void temporarilyHide() {
//    }
//
//    public void mouseDragged(MouseEvent e) {
//        this.handleMouseDrag(e.getX(), e.getY(), e.getModifiers());
//    }
//
//    public void mouseMoved(MouseEvent e) {
//    }
//
//    public void mouseReleased(MouseEvent e) {
//        this.handleMouseUp(e.getX(), e.getY());
//    }
//
//    public double getXBase() {
//        return this.bounds != null ? this.bounds.x : (double)this.x;
//    }
//
//    public double getYBase() {
//        return this.bounds != null ? this.bounds.y : (double)this.y;
//    }
//
//    public double getFloatWidth() {
//        return this.bounds != null ? this.bounds.width : (double)this.width;
//    }
//
//    public double getFloatHeight() {
//        return this.bounds != null ? this.bounds.height : (double)this.height;
//    }
//
//    public double getAngle() {
//        return 0.0D;
//    }
//
//    public void enableSubPixelResolution() {
//        this.bounds = new Rectangle2D.Double(this.getXBase(), this.getYBase(), this.getFloatWidth(), this.getFloatHeight());
//        this.subPixel = true;
//    }
//
//    public String getDebugInfo() {
//        return "";
//    }
//
//    public int getHashCode() {
//        return this.hashCode() ^ (new java.lang.Double(this.getXBase())).hashCode() ^ Integer.rotateRight((new java.lang.Double(this.getYBase())).hashCode(), 16);
//    }
//
//    static {
//        ROIColor = Prefs.getColor("roicolor", Color.yellow);
//        pasteMode = 0;
//        lineWidth = 1;
//    }
//}
//
