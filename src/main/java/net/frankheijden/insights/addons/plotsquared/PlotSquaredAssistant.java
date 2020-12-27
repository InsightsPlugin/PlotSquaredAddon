package net.frankheijden.insights.addons.plotsquared;

import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.frankheijden.insights.entities.Area;
import net.frankheijden.insights.entities.CacheAssistant;
import net.frankheijden.insights.entities.CuboidSelection;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PlotSquaredAssistant extends CacheAssistant {

    public PlotSquaredAssistant() {
        super("PlotSquared", "PlotSquared", "plot", "1.0.0");
    }
    
    public String getId(Collection<? extends Plot> plots) {
        return getPluginName() + "@" + getRawId(plots);
    }

    private String getRawId(Collection<? extends Plot> plots) {
        StringBuilder sb = new StringBuilder();
        for (Plot plot : plots) {
            sb.append(";").append(plot.getId().toCommaSeparatedString());
        }
        return sb.substring(1);
    }
    
    public Area adapt(Plot basePlot) {
        if (basePlot == null) return null;

        Set<CuboidRegion> regions = basePlot.getRegions();
        List<CuboidSelection> selections = new ArrayList<>(regions.size());

        World world = Bukkit.getWorld(basePlot.getWorldName());
        if (world == null) return null;

        for (CuboidRegion region : regions) {
            selections.add(new CuboidSelection(
                    BukkitAdapter.adapt(world, region.getMinimumPoint()),
                    BukkitAdapter.adapt(world, region.getMaximumPoint())
            ));
        }

        if (selections.isEmpty()) return null;
        return Area.from(this, getId(basePlot.getConnectedPlots()), selections);
    }

    @Override
    public Area getArea(org.bukkit.Location bukkitLoc) {
        if (bukkitLoc == null) return null;
        return adapt(new Location(
                bukkitLoc.getWorld().getName(),
                bukkitLoc.getBlockX(),
                bukkitLoc.getBlockY(),
                bukkitLoc.getBlockZ()
        ).getOwnedPlotAbs());
    }
}
