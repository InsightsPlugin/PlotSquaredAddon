package dev.frankheijden.insights.addons.plotsquared;

import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import dev.frankheijden.insights.api.addons.InsightsAddon;
import dev.frankheijden.insights.api.addons.Region;
import dev.frankheijden.insights.api.addons.SimpleMultiCuboidRegion;
import dev.frankheijden.insights.api.objects.math.Cuboid;
import dev.frankheijden.insights.api.objects.math.Vector3;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PlotSquaredAddon implements InsightsAddon {
    
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
    
    public Optional<Region> adapt(Plot basePlot, World world) {
        if (basePlot == null) return Optional.empty();

        Set<CuboidRegion> regions = basePlot.getRegions();
        if (regions.isEmpty()) return Optional.empty();

        List<Cuboid> cuboids = new ArrayList<>(regions.size());
        for (CuboidRegion region : regions) {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            cuboids.add(new Cuboid(
                    world,
                    new Vector3(min.getX(), min.getY(), min.getZ()),
                    new Vector3(max.getX(), max.getY(), max.getZ())
            ));
        }

        return Optional.of(new SimpleMultiCuboidRegion(
                cuboids,
                getPluginName(),
                getId(basePlot.getConnectedPlots())
        ));
    }

    @Override
    public String getPluginName() {
        return "PlotSquared";
    }

    @Override
    public String getAreaName() {
        return "plot";
    }

    @Override
    public String getVersion() {
        return "{version}";
    }

    @Override
    public Optional<Region> getRegion(org.bukkit.Location location) {
        return adapt(new Location(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        ).getOwnedPlotAbs(), location.getWorld());
    }
}
