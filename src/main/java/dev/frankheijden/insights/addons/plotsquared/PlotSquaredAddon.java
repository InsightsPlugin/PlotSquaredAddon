package dev.frankheijden.insights.addons.plotsquared;

import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import dev.frankheijden.insights.api.addons.InsightsAddon;
import dev.frankheijden.insights.api.addons.Region;
import dev.frankheijden.insights.api.objects.chunk.ChunkPart;
import dev.frankheijden.insights.api.objects.math.Cuboid;
import dev.frankheijden.insights.api.objects.math.Vector3;
import org.bukkit.World;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Cuboid adapt(CuboidRegion region, World world) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        return new Cuboid(
                world,
                new Vector3(min.getX(), min.getY(), min.getZ()),
                new Vector3(max.getX(), max.getY(), max.getZ())
        );
    }
    
    public Optional<Region> adapt(Plot basePlot, World world) {
        if (basePlot == null) return Optional.empty();

        Set<CuboidRegion> cuboids = basePlot.getRegions();
        if (cuboids.isEmpty()) return Optional.empty();

        return Optional.of(new PlotSquaredRegion(
                world,
                cuboids,
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
        return adapt(Location.at(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        ).getOwnedPlotAbs(), location.getWorld());
    }

    public class PlotSquaredRegion implements Region {

        private final World world;
        private final Set<CuboidRegion> cuboids;
        private final String key;

        public PlotSquaredRegion(World world, Set<CuboidRegion> cuboids, String key) {
            this.world = world;
            this.cuboids = cuboids;
            this.key = key;
        }

        @Override
        public String getAddon() {
            return getPluginName();
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public List<ChunkPart> toChunkParts() {
            return cuboids.stream()
                    .flatMap(cuboid -> adapt(cuboid, world).toChunkParts().stream())
                    .collect(Collectors.toList());
        }
    }
}
