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
        public const string IsDeafultRedisPortInUse = "IS_DEFAULT_REDIS_PORT_IN_USE";
        public const string IsDeafultServerPortInUse = "IS_DEFAULT_SERVER_PORT_IN_USE";
        public const string DeafultRedisPort = "DEFAULT_REDIS_PORT";
        public const string DeafultServerPort = "DEFAULT_SERVER_PORT";
        public const string NewRedisPort = "NEW_REDIS_PORT";
        public const string NewServerPort = "NEW_SERVER_PORT";
    }

    public class HawkCDAgentProperties : CommonProperties
    {
        public const string ServerAddress = "SERVER_ADDRESS";
        public const string ServerPort = "SERVER_Port";
    }
}
