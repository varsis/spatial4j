package com.spatial4j.core.shape.impl;

import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.exception.InvalidShapeException;
import com.spatial4j.core.shape.Point;

/**
 * Created by Chris Pavlicek on 2/7/2014.
 */
public class GreatCircle {


  private Point a;
  private Point b;

  final Point3d a3d;
  final Point3d b3d;

  // TODO: inline vector class? or build another?
  final Point3d planeVector;

  final double invPlaneLength;

  public GreatCircle(Point a, Point b) {
    this.a = a;
    this.b = b;

    if(b.getY() == (-1*a.getY())) {

      double xA = a.getX() - 180;
      if(xA < -180) {
        xA += 360;
      } else if(xA == 180){
        xA = -180;
      }

      double xB = b.getX();

      if(xB == 180) {
        xB = -180;
      }

      if(xA == xB || (a.getX() == b.getX() && a.getY() == b.getY())) {
        throw new InvalidShapeException("Antipodal points ambiguous great circle");
      }
    }

    // Store points of the great circle
    a3d = new Point3d(a);
    b3d = new Point3d(b);

    // Vector as Point3d for simplicity.
    planeVector = Point3d.crossProductPoint(a3d,b3d);

    // Inverse of plane length
    invPlaneLength = 1/GreatCircle.vectorLength(planeVector);
  }

  public GreatCircle(Point3d a, Point3d b) {
    // Store points of the great circle
    a3d = a;
    b3d = b;

    // Vector as Point3d for simplicity.
    planeVector = Point3d.crossProductPoint(a3d,b3d);

    // Inverse of plane length
    invPlaneLength = 1/GreatCircle.vectorLength(planeVector);
  }

  /**
   * Returns the distance to the GreatCircle from the Point c.
   * Also known as the cross-track distance.
   * See Ref: http://mathworld.wolfram.com/Point-PlaneDistance.html
   * @param c
   * @return
   */
  public double distanceToPoint(Point c) {
    Point3d c3d = new Point3d(c);
    double height = GreatCircle.dotProduct(planeVector, c3d)*invPlaneLength;

    // opposite/hyp = Sin theta -> use asin of Height/1 (radians)
    // Gives radians of arc length.
    return Math.abs(DistanceUtils.toDegrees(Math.asin(height)));
  }

  /** the dot product of a vector and a point. (plane.x * point.x + plane.y * point.y + plane.z * point.z) */
  private static double dotProduct(Point3d vectorPlane, Point3d point) {
    return vectorPlane.getX()*point.getX() + vectorPlane.getY()*point.getY() + vectorPlane.getZ()*point.getZ();
  }

  public Point3d getA() {
    return a3d;
  }

  public Point3d getB() {
    return b3d;
  }

  public Point getPointA() {
    return a;
  }

  public Point getPointB() {
    return b;
  }

  /** The magnitude of the vector. sqrt(x^2 + y^2 + z^2) */
  private static double vectorLength(Point3d p) {
    double x2 = p.getX() * p.getX();
    double y2 = p.getY() * p.getY();
    double z2 = p.getZ() * p.getZ();

    return Math.sqrt(x2 + y2 + z2);
  }


}
