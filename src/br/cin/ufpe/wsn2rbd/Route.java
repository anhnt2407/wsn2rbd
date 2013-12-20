package br.cin.ufpe.wsn2rbd;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author avld
 */
public class Route
{
    private String name;
    private double availability;
    private List<Route> routeList;
    
    public Route()
    {
        routeList = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getAvailability()
    {
        return availability;
    }

    public void setAvailability(double availability)
    {
        this.availability = availability;
    }

    public List<Route> getRouteList()
    {
        return routeList;
    }

    public void setRouteList(List<Route> routeList)
    {
        if( routeList == null )
        {
            this.routeList.clear();
        }
        else
        {
            this.routeList = routeList;
        }
    }
    
}
