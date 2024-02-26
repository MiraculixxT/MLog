package de.miraculixx.mlog;

import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.function.Consumer;

abstract public class MLogAPI {
    /**
     * The instance of the MLogAPI.
     * This is set by the local installed MLog mod/plugin and should not be set by any other mod/plugin.
     * <br>
     * If MLog is not installed, this will be null.
     */
    public static MLogAPI INSTANCE = null;

    /**
     * Define a new log endpoint for your mod to let users send all important logs/configs to you without explaining them what you need and how to get it.
     * <br><br>
     * If registered, you can request the log from the user by giving them a unique code (explained below).
     * The user simply needs to enter <span style="background:#1D1F20;color:#7C7B7B;font-weight:bold">/msend {@code <mod-id> <code>}</span> in the chat or console (user will still be prompted that provided files will be sent).<br>
     * <br><br>
     * <span style="font-weight:bold;font-size:larger">● Webhook & code usage</span><br>
     * Webhooks are any URLs that can receive POST requests.
     * Please note, your webhook URL will be visible to users if they use certain tools to extract the code.
     * Therefore, we send the provided code as an url parameter and in the json body to verify the request (details at the end).
     * <br><br><br>
     * <span style="font-weight:bold;font-size:larger">● 1. Discord Bot</span><br>
     * If you want your logs/config to be sent to a discord channel, you can use <a href="https://mutils.net/mlog/dcbot">our bot</a> to handle all requests.
     * With the bot you can enter <span style="background:#1D1F20;color:#7C7B7B;font-weight:bold">/code {@code <mod-id> [<channel/thread>] [<announce>]}</span> in Discord to create a new request code and give it the user.<br>
     * Providing no target channel will use the channel the command was executed in.
     * <br><br>
     * Every code is only valid for the defined time in the bot settings and can only be used once.
     * See more details <a href="https://mutils.net/mlog/dcbot">here</a>.
     * <br><br>
     * <span style="font-weight:bold">Webhook</span> - <span style="background:#1D1F20;color:#7C7B7B;font-weight:bold">{@code https://mlog.mutils.net/webhook/<mod-id>/<guild-id>}</span>
     * <br><br><br>
     * <span style="font-weight:bold;font-size:larger">● 2. Custom backend</span><br>
     * If you want to handle the requests yourself, you can use any backend that can receive POST requests (e.g. <a href="https://ktor.io/docs/intellij-idea.html">Ktor</a>).
     * <br><br>
     * It's important to verify each request with the provided code and timestamp to prevent abuse.
     * Also manage timeouts and rate limits to prevent spam.
     * <br><br>
     * MWeb sends a multipart body with a part containing all requests information in json format and a part for each file.
     * Look into the <a href="https://mutils.net/mlog/custom/">docs</a> for a full preview of the request and example.
     *
     * @param modInstance The main class of your mod/plugin. For paper, the class must extend JavaPlugin or similar. For fabric, the class must implement ModInitializer or similar.
     * @param modID The unique ID/name of your mod/plugin. This will be used to identify the mod/plugin in requests and will be prompted to the user.
     * @param webhookURL The URL that will receive the POST requests.
     * @param files All files that should be sent to the webhook. If zipping is enabled, only one file will be sent. Files are restricted to the following locations for security: /logs, /plugins, /config, /mods
     * @param zip If true, all files will be zipped before sending. This will reduce the number of files sent to the webhook to one and reduce upload size but will not render as preview in Discord.
     * @return True if the registration was successful, false if the modInstance is already registered or does not match given mod id.
     * @see MLogAPI#unregisterLogSending(Object, String)
     */
     public abstract Boolean registerLogSending(Object modInstance, String modID, URL webhookURL, Set<File> files, Boolean zip);

    /**
     * Unregister your mod/plugin from MLog.
     * This will remove all local configurations and prevent users from sending logs to you.
     * <br><br>
     * <span style="font-weight:bold;font-size:larger">● IMPORTANT</span><br>
     * For plugins, use this method in your onDisable method to prevent users from sending logs after the plugin is disabled.
     * This will fix any issues with the /reload command and prevent errors while MWeb searches for your plugin while it's disabled.
     * <br><br>
     * For mods, using this method on disabling is advised but not required because fabric handles mod loading without any known issues even on reloads.
     *
     * @param modInstance The main class of your mod/plugin. For paper, the class must extend JavaPlugin or similar. For fabric, the class must implement ModInitializer or similar.
     * @param modID The unique ID/name of your mod/plugin.
     * @return True if the unregistration was successful, false if the modInstance is not registered or does not match given mod id.
     */
    public abstract Boolean unregisterLogSending(Object modInstance, String modID);
}
