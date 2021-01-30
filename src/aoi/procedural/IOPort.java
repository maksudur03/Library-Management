/* This is the graphical representation of an input or output port on a module. *//* Copyright (C) 2000 by Peter Eastman   This program is free software; you can redistribute it and/or modify it under the   terms of the GNU General Public License as published by the Free Software   Foundation; either version 2 of the License, or (at your option) any later version.   This program is distributed in the hope that it will be useful, but WITHOUT ANY    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A    PARTICULAR PURPOSE.  See the GNU General Public License for more details. */package aoi.procedural;import java.awt.*;public class IOPort{  int x, y, type, direction, location;  String description[];  Rectangle bounds;  Module module;    public static final int INPUT = 0;  public static final int OUTPUT = 1;  public static final int NUMBER = 0;  public static final int COLOR = 1;  public static final int TOP = 0;  public static final int BOTTOM = 1;  public static final int LEFT = 2;  public static final int RIGHT = 3;  public static final int SIZE = 5;    public IOPort(int type, int direction, int location, String description[])  {    this.type = type;    this.direction = direction;    this.location = location;    this.description = description;  }    /** Get the port's screen position. */  public Point getPosition()  {    return new Point(x, y);  }  /** Set the port's screen position. */    public void setPosition(int x, int y)  {    this.x = x;    this.y = y;    if (location == TOP)      bounds = new Rectangle(x-SIZE+1, y, 2*SIZE-1, SIZE);    else if (location == BOTTOM)      bounds = new Rectangle(x-SIZE+1, y-SIZE, 2*SIZE-1, SIZE);    if (location == LEFT)      bounds = new Rectangle(x, y-SIZE+1, SIZE, 2*SIZE-1);    else if (location == RIGHT)      bounds = new Rectangle(x-SIZE, y-SIZE+1, SIZE, 2*SIZE-1);  }    /** Get the type of value for this port. */    public int getValueType()  {    return type;  }    /** Get the type of port this is (input or output). */    public int getType()  {    return direction;  }    /** Get the location of this port (top, bottom, left, or right). */    public int getLocation()  {    return location;  }    /** Get the module this port belongs to. */    public Module getModule()  {    return module;  }    /** Set the module this port belongs to. */    public void setModule(Module mod)  {    module = mod;  }  /** Determine whether a point on the screen is inside this port. */    public boolean contains(Point p)  {    return bounds.contains(p);  }    /** Get the description of this port. */    public String [] getDescription()  {    return description;  }    /** Set the description of this port. */    public void setDescription(String desc[])  {    description = desc;  }    /** Draw the port. */    public void draw(Graphics g)  {    if (type == NUMBER)      g.setColor(Color.BLACK);    else      g.setColor(Color.BLUE);    if (location == TOP)      g.fillPolygon(new int[] {x+SIZE, x-SIZE, x}, new int[] {y, y, y+SIZE}, 3);    else if (location == BOTTOM)      g.fillPolygon(new int[] {x+SIZE, x-SIZE, x}, new int[] {y, y, y-SIZE}, 3);    else if (location == LEFT)      g.fillPolygon(new int[] {x, x, x+SIZE}, new int[] {y+SIZE, y-SIZE, y}, 3);    else if (location == RIGHT)      g.fillPolygon(new int[] {x-SIZE, x-SIZE, x}, new int[] {y+SIZE, y-SIZE, y}, 3);  }}