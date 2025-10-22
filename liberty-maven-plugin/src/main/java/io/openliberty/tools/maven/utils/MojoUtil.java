package io.openliberty.tools.maven.utils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Utility class for Maven Mojo operations
 */
public class MojoUtil {
    public static void runMojo(String groupId, String artifactId, String goal,
                              MavenProject project, MavenSession session,
                              BuildPluginManager pluginManager, Log log)
                              throws MojoExecutionException {
        Plugin plugin = getPlugin(groupId, artifactId, project);
        Xpp3Dom config = ExecuteMojoUtil.getPluginGoalConfig(plugin, goal, log);
        log.info("Running " + artifactId + ":" + goal);
        log.debug("configuration:\n" + config);
        executeMojo(plugin, goal(goal), config, executionEnvironment(project, session, pluginManager));

        // TODO: All the runMojo methods should be pointed to this one in future
    }

    private static Plugin getPlugin(String groupId, String artifactId, MavenProject project) {
        Plugin plugin = project.getPlugin(groupId + ":" + artifactId);
        if (plugin == null) {
            plugin = getPluginFromPluginManagement(groupId, artifactId, project);
        }
        if (plugin == null) {
            plugin = plugin(groupId(groupId), artifactId(artifactId), version("RELEASE"));
        }
        return plugin;
    }

    private static Plugin getPluginFromPluginManagement(String groupId, String artifactId, MavenProject project) {
        Plugin result = null;
        PluginManagement pluginManagement = project.getPluginManagement();
        if (pluginManagement != null) {
            for (Plugin plugin : pluginManagement.getPlugins()) {
                if (groupId.equals(plugin.getGroupId()) && artifactId.equals(plugin.getArtifactId())) {
                    result = plugin;
                    break;
                }
            }
        }
        return result;
    }
}