using Microsoft.Deployment.WindowsInstaller;
using System;
using System.IO;
using System.Linq;

namespace CustomActions
{
    public class CustomActions
    {
        [CustomAction]
        public static ActionResult InstallRedis(Session session)
        {
            session.Log("Begin InstallRedis");
            try
            {
                var redisFolder = Utils.GetPathToFileInInstalDir(session, "redis");
                var servicePort = session[HawkCDServerProperties.DeafultRedisPort];

                if (session[HawkCDServerProperties.IsDeafultRedisPortInUse] == "1" && !string.IsNullOrEmpty(session[HawkCDServerProperties.NewRedisPort]))
                    servicePort = session[HawkCDServerProperties.NewRedisPort];

                Utils.ExecuteCommand(string.Format("redis-server --service-install redis.windows-service.conf --service-name {0} --port {1}", Constants.RedisServiceName, servicePort), session, redisFolder);
                Utils.ExecuteCommand(string.Format("redis-server --service-start --service-name {0}", Constants.RedisServiceName), session, redisFolder);

            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended InstallRedis");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UpdateRedisConfig(Session session)
        {
            session.Log("Begin UpdateRedisConfig");
            try
            {
                var redisConfigFilea = new string[]
            {
                 Utils.GetPathToFileInInstalDir(session, "redis\\redis.windows.conf"),
                 Utils.GetPathToFileInInstalDir(session, "redis\\redis.windows-service.conf")
            };

                foreach (var redisConfigFile in redisConfigFilea)
                {
                    if (File.Exists(redisConfigFile))
                    {
                        var lines = File.ReadAllLines(redisConfigFile);

                        var updatedLines = 0;

                        for (int i = 0; i < lines.Length; i++)
                        {
                            var line = lines[i].Trim();
                            if (line.StartsWith("save ", StringComparison.CurrentCultureIgnoreCase))
                            {
                                if (updatedLines < 3)
                                {
                                    if (updatedLines == 0)
                                    {
                                        line = "save 5 1";
                                    }
                                    else if (updatedLines == 1)
                                    {
                                        line = "save 2 10";
                                    }
                                    else
                                    {
                                        line = "save 1 100";
                                    }

                                    updatedLines++;
                                }
                                else
                                {
                                    line = "#   " + line;
                                }

                                lines[i] = line;
                            }
                        }

                        File.WriteAllLines(redisConfigFile, lines);
                    }
                    else
                    {
                        session.Log("Redis config file not found under: {0}", redisConfigFile);
                    }
                }
            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended UpdateRedisConfig");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UninstallRedis(Session session)
        {
            session.Log("Begin UninstallRedis");

            try
            {
                var redisFolder = Utils.GetPathToFileInInstalDir(session, "redis");

                Utils.ExecuteCommand(string.Format("redis-server --service-stop --service-name {0}", Constants.RedisServiceName), session, redisFolder);
                Utils.ExecuteCommand(string.Format("redis-server --service-uninstall --service-name {0}", Constants.RedisServiceName), session, redisFolder);
            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended UninstallRedis");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UpdateServerConfigFile(Session session)
        {
            session.Log("Begin UpdateServerConfigFile");
            try
            {
                var configFilePath = Utils.GetPathToFileInInstalDir(session, "config.yaml");

                if (File.Exists(configFilePath))
                {
                    session.Log("Updating config file: {0}", configFilePath);
                    session.Log("Updating key serverHost with new value: {0}", session[HawkCDServerProperties.HostName]);
                    Utils.ReplaceYamlKeyValue(configFilePath, "serverHost", session[HawkCDServerProperties.HostName]);

                    if (session[HawkCDServerProperties.IsDeafultRedisPortInUse] == "1")
                    {
                        var servicePort = session[HawkCDServerProperties.NewRedisPort];
                        session.Log("Updating key port with new value: {0}", servicePort);
                        Utils.ReplaceYamlKeyValue(configFilePath, "port", servicePort);
                    }

                    if (session[HawkCDServerProperties.IsDeafultServerPortInUse] == "1")
                    {
                        var servicePort = session[HawkCDServerProperties.NewServerPort];
                        session.Log("Updating key serverPort with new value: {0}", servicePort);
                        Utils.ReplaceYamlKeyValue(configFilePath, "serverPort", servicePort);
                    }

                }
                else
                {
                    session.Log("Config file does not exists: {0}", configFilePath);
                }
            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }
            session.Log("Ended UpdateServerConfigFile");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult BackupConfigFiles(Session session)
        {
            var actionName = "BackupConfigFile";

            session.Log("Begin {0}", actionName);
            try
            {
                if (Utils.GetInstallerType(session) == InstallerType.Server)
                {
                    Utils.CreateFileBackup(Utils.GetPathToFileInInstalDir(session, "config.yaml"), session);
                    Utils.CreateFileBackup(Utils.GetPathToFileInInstalDir(session, "redis\\redis.windows.conf"), session);
                    Utils.CreateFileBackup(Utils.GetPathToFileInInstalDir(session, "redis\\redis.windows-service.conf"), session);
                }
                else
                {
                    Utils.CreateFileBackup(Utils.GetPathToFileInInstalDir(session, "config\\config.properties"), session);
                }
            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }
            session.Log("Ended {0}", actionName);

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult RestoreConfigFiles(Session session)
        {
            var actionName = "RestoreConfigFiles";

            session.Log("Begin {0}", actionName);
            try
            {
                if (Utils.GetInstallerType(session) == InstallerType.Server)
                {
                    Utils.ReplaceFileFromBackup(Utils.GetPathToFileInInstalDir(session, "config.yaml"), session);
                    Utils.ReplaceFileFromBackup(Utils.GetPathToFileInInstalDir(session, "redis\\redis.windows-service.conf"), session);
                }
                else
                {
                    Utils.ReplaceFileFromBackup(Utils.GetPathToFileInInstalDir(session, "config\\config.properties"), session);
                }
            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }
            session.Log("Ended {0}", actionName);

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UpdateAgentConfigFile(Session session)
        {
            session.Log("Begin UpdateServerConfigFile");
            try
            {
                var isntallDir = session[CommonProperties.InstallDir].TrimEnd('\\');
                var configFilePath = isntallDir + "\\config\\config.properties";

                if (File.Exists(configFilePath))
                {
                    session.Log("Updating config file: {0}", configFilePath);
                    session.Log("Updating key serverName with new value: {0}", session[HawkCDAgentProperties.ServerAddress]);
                    Utils.ReplacePropertiesKeyValue(configFilePath, "serverName", session[HawkCDAgentProperties.ServerAddress]);
                    session.Log("Updating key serverPort with new value: {0}", session[HawkCDAgentProperties.ServerPort]);
                    Utils.ReplacePropertiesKeyValue(configFilePath, "serverPort", session[HawkCDAgentProperties.ServerPort]);
                }
                else
                {
                    session.Log("Config file does not exists: {0}", configFilePath);
                }
            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }
            session.Log("Ended UpdateServerConfigFile");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult DetectJava(Session session)
        {
            session.Log("Begin DetectJavaHome");

            try
            {
                if (Utils.IsJavaInstalled())
                {
                    session.Log("Setting  IsJavaInstalled to: 1");
                    session[CommonProperties.IsJavaInstalled] = "1";
                }
                else
                {
                    session.Log("Setting  IsJavaInstalled to: 0");
                    session[CommonProperties.IsJavaInstalled] = "0";
                }
            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended DetectJavaHome");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult CheckRedisPort(Session session)
        {
            session.Log("Begin CheckRedisPort");
            try
            {
                int port = int.Parse(session[HawkCDServerProperties.DeafultRedisPort]);

                if (Utils.IsPortInUse(port))
                {
                    session.Log("Port {0} is in use.", port);
                    session[HawkCDServerProperties.IsDeafultRedisPortInUse] = "1";
                }
                else
                {
                    session.Log("Port {0} is in free.", port);
                    session[HawkCDServerProperties.IsDeafultRedisPortInUse] = "0";
                }


            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended CheckRedisPort");

            return ActionResult.Success;
        }


        [CustomAction]
        public static ActionResult CheckServerPort(Session session)
        {
            session.Log("Begin CheckServerPort");
            try
            {
                int port = int.Parse(session[HawkCDServerProperties.DeafultServerPort]);

                if (Utils.IsPortInUse(port))
                {
                    session.Log("Port {0} is in use.", port);
                    session[HawkCDServerProperties.IsDeafultServerPortInUse] = "1";
                }
                else
                {
                    session.Log("Port {0} is in free.", port);
                    session[HawkCDServerProperties.IsDeafultServerPortInUse] = "0";
                }


            }
            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended CheckServerPort");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UpdateServiceAppConfig(Session session)
        {
            session.Log("Begin UpdateServerServiceAppConfig");
            try
            {
                var installerType = Utils.GetInstallerType(session);
                var isntallDir = session[CommonProperties.InstallDir].TrimEnd('\\');
                var configFilePath = isntallDir + "\\HawkCDServiceWrapper.exe.config";

                session.Log("Installer type is:  {0}", installerType);

                if (!File.Exists(configFilePath))
                {
                    session.Log("File does not exist: {0}", configFilePath);
                    return ActionResult.Success;
                }

                var jarFile = Directory.GetFiles(isntallDir, "*.jar").FirstOrDefault();
                jarFile = Path.GetFileName(jarFile);

                string[] values = null;

                if (installerType == InstallerType.Server)
                {
                    values = new string[]
                    {
                        "StartProcessName", "java.exe",
                        "StartArgs", "-jar " + jarFile,
                        "EventSourceName", "HawkCDServer",
                        "ServiceName", Utils.GenerateServiceName("HawkCDServer"),
                        "WorkingDirectory", isntallDir,
                    };
                }
                else
                {
                    values = new string[]
                    {
                        "StartProcessName", "java.exe",
                        "StartArgs", "-jar " + jarFile,
                        "EventSourceName", "HawkCDAgent",
                        "ServiceName", Utils.GenerateServiceName("HawkCDAgent"),
                        "WorkingDirectory", isntallDir,
                    };
                }
                session.Log("Starting to update config: {0}", configFilePath);
                Utils.UpdateAppConfig(configFilePath, values);
            }

            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended UpdateServiceAppConfig");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult InstallService(Session session)
        {
            session.Log("Begin InstallService");

            try
            {
                var installerType = Utils.GetInstallerType(session);
                var configFilePath = Utils.GetPathToFileInInstalDir(session, "HawkCDServiceWrapper.exe.config");
                var serviceWrapperExe = Utils.GetPathToFileInInstalDir(session, "HawkCDServiceWrapper.exe");

                if (!File.Exists(configFilePath))
                {
                    session.Log("File does not exist: {0}", configFilePath);
                    return ActionResult.Success;
                }

                var serviceName = Utils.GetServiceNameFromAppConfig(configFilePath);
                var installCommand = string.Format("sc create {0} displayname= \"HawkCD {1} Service\" binpath= \"{2}\" start= auto", serviceName, installerType, serviceWrapperExe);

                session.Log("Installing service: {0}", installCommand);
                Utils.ExecuteCommand(installCommand, session);

                session.Log("Starting service: {0}", serviceName);
                if (!ServiceUtils.StartService(serviceName))
                {
                    session.Log("Starting service failed: {0}", serviceName);
                }
            }

            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended InstallService");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UninstallService(Session session)
        {
            session.Log("Begin InstallService");
            try
            {
                var configFilePath = Utils.GetPathToFileInInstalDir(session, "HawkCDServiceWrapper.exe.config");

                if (!File.Exists(configFilePath))
                {
                    session.Log("File does not exist: {0}", configFilePath);
                    return ActionResult.Success;
                }

                var serviceName = Utils.GetServiceNameFromAppConfig(configFilePath);
                var uninstallCommand = string.Format("sc delete {0} ", serviceName);

                session.Log("Stopping service: {0}", serviceName);
                if (!ServiceUtils.StopService(serviceName))
                {
                    session.Log("Stopping service failed: {0}", serviceName);
                }

                session.Log("Uninstall service: {0}", uninstallCommand);
                Utils.ExecuteCommand(uninstallCommand, session);
            }

            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended InstallService");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult StopService(Session session)
        {
            session.Log("Begin StopService");
            try
            {
                var installerType = Utils.GetInstallerType(session);
                var isntallDir = session[CommonProperties.InstallDir].TrimEnd('\\');
                var configFilePath = isntallDir + "\\HawkCDServiceWrapper.exe.config";

                if (!File.Exists(configFilePath))
                {
                    session.Log("File does not exist: {0}", configFilePath);
                    return ActionResult.Success;
                }

                var serviceName = Utils.GetServiceNameFromAppConfig(configFilePath);

                session.Log("Stopping service: {0}", serviceName);

                if (!ServiceUtils.StopService(serviceName))
                    session.Log("Stopping service failed: {0}", serviceName);
                else
                    session.Log("Service: {0} was stopped.", serviceName);

                if (installerType == InstallerType.Server)
                {
                    serviceName = Constants.RedisServiceName;

                    session.Log("Stopping service: {0}", serviceName);

                    if (!ServiceUtils.StopService(serviceName))
                        session.Log("Stopping service failed: {0}", serviceName);
                    else
                        session.Log("Service: {0} was stopped.", serviceName);
                }
            }

            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended StopService");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult CheckIsUserAdministrator(Session session)
        {
            if (!Utils.IsUserAdministrator())
            {
                Record record = new Record();
                record.FormatString = "Administrator privileges are required to run this setup. Please use admin command prompt to escalate the installer, or use the administrator account.";
                session.Message(InstallMessage.Warning | (InstallMessage)MessageButtons.OK, record);

                return ActionResult.Failure;
            }

            return ActionResult.Success;
        }
    }
}
