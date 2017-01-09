namespace CustomActions
{

    public class CommonProperties
    {
        public const string InstallDir = "INSTALLDIR";
        public const string IsJavaInstalled = "IS_JAVA_INSTALLED";
        public const string InstallerType = "INSTALLER_TYPE";
    }

    public class HawkCDServerProperties : CommonProperties
    {
        public const string HostName = "HOST_NAME";
        public const string IsDeafultServerPortInUse = "IS_DEFAULT_SERVER_PORT_IN_USE";
        public const string DeafultServerPort = "DEFAULT_SERVER_PORT";
        public const string NewServerPort = "NEW_SERVER_PORT";

        public const string IsSingleInstanceInstallation = "IS_SINGLE_INSTANCE_INSTALLATION";

        public const string RedisHostName = "REDIS_HOST_NAME";
        public const string RedisPassword = "REDIS_PASSWORD";
        public const string RedisPort = "REDIS_PORT";
        public const string RedisUserName = "REDIS_USER_NAME";

        public const string MongoDbHostName = "MONGODB_HOST_NAME";
        public const string MongoDbPassword = "MONGODB_PASSWORD";
        public const string MongoDbPort = "MONGODB_PORT";
        public const string MongoDbUserName = "MONGODB_USER_NAME";

    }

    public class HawkCDAgentProperties : CommonProperties
    {
        public const string ServerAddress = "SERVER_ADDRESS";
        public const string ServerPort = "SERVER_PORT";
    }
}
