package kr.lucymc.lucy_playtime;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Objects;

public final class Lucy_PlayTime extends JavaPlugin {
    private static Lucy_PlayTime INSTANCE;
    public static Lucy_PlayTime getInstance() {
        return INSTANCE;
    }
    public static Connection connection;
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static HashMap<String, Integer> PlayTimes = new HashMap<>();
    FileConfiguration config = this.getConfig();
    @Override
    public void onEnable() {
        File ConfigFile = new File(getDataFolder(), "config.yml");
        if(!ConfigFile.isFile()){
            config.addDefault("DB_ID", "root");
            config.addDefault("DB_PW", "INTY");
            config.addDefault("DB_URL", "jdbc:mysql://127.0.0.1:3307/lucy?autoReconnect=true");
            config.options().copyDefaults(true);
            saveConfig();
        }
        getServer().getPluginManager().registerEvents(new PlayTime_Event(), this);
        INSTANCE = this;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("DB_URL")), config.getString("DB_ID"), config.getString("DB_PW"));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayTime_PAPI(this).register();
        } else {
            getLogger().severe("PlaceholderAPI 플러그인을 찾을 수 없습니다.");
        }
    }

    @Override
    public void onDisable() {
        try { // using a try catch to catch connection errors (like wrong sql password...)
            if (connection != null && !connection.isClosed()) { // checking if connection isn't null to
                connection.close(); // closing the connection field variable.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
