using Microsoft.Deployment.WindowsInstaller;
using System;
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Text.RegularExpressions;

namespace CustomActions
{
    public class CustomActions
    {
        const string RedisServiceName = "HawkCDRedis";

        #region Server Custom Actions 

        [CustomAction]
        public static ActionResult InstallRedis(Session session)
        {
            session.Log("Begin InstallRedis");

            var isntallDir = session[HawkCDServerProperties.InstallDir].TrimEnd('\\');
            var redisFolder = isntallDir + "\\redis";

            var servicePort = session[HawkCDServerProperties.DeafultRedisPort];

            if (session[HawkCDServerProperties.IsDeafultRedisPortInUse] == "1")
                servicePort = session[HawkCDServerProperties.NewRedisPort];

            ExecuteCommand(string.Format("redis-server --service-install redis.windows-service.conf --service-name {0} --port {1}", RedisServiceName, servicePort), session, redisFolder);
            ExecuteCommand(string.Format("redis-server --service-start --service-name {0}", RedisServiceName), session, redisFolder);

            session.Log("Ended InstallRedis");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UninstallRedis(Session session)
        {
            session.Log("Begin UninstallRedis");

            var isntallDir = session[HawkCDServerProperties.InstallDir].TrimEnd('\\');
            var redisFolder = isntallDir + "\\redis";

            ExecuteCommand(string.Format("redis-server --service-stop --service-name {0}", RedisServiceName), session, redisFolder);
            ExecuteCommand(string.Format("redis-server --service-uninstall --service-name {0}", RedisServiceName), session, redisFolder);

            session.Log("Ended UninstallRedis");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UpdateServerConfigFile(Session session)
        {
            session.Log("Begin UpdateServerConfigFile");

            var isntallDir = session[HawkCDServerProperties.InstallDir].TrimEnd('\\');
            var configFilePath = isntallDir + "\\config.yaml";

            if (File.Exists(configFilePath))
            {
                session.Log("Updating config file: {0}", configFilePath);
                session.Log("Updating key host with new value: {0}", session[HawkCDServerProperties.HostName]);
                ReplaceYamlKeyValue(configFilePath, "host", session[HawkCDServerProperties.HostName]);


                if (session[HawkCDServerProperties.IsDeafultRedisPortInUse] == "1")
                {
                    var servicePort = session[HawkCDServerProperties.NewRedisPort];
                    session.Log("Updating key port with new value: {0}", servicePort);
                    ReplaceYamlKeyValue(configFilePath, "port", servicePort);
                }

            }
            else
            {
                session.Log("Config file does not exists: {0}", configFilePath);
            }

            session.Log("Ended UpdateServerConfigFile");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UpdateAgentConfigFile(Session session)
        {
            session.Log("Begin UpdateServerConfigFile");

            var isntallDir = session[HawkCDServerProperties.InstallDir].TrimEnd('\\');
            var configFilePath = isntallDir + "\\config\\config.properties";

            if (File.Exists(configFilePath))
            {
                session.Log("Updating config file: {0}", configFilePath);
                session.Log("Updating key serverName with new value: {0}", session[HawkCDAgentProperties.ServerAddress]);
                ReplacePropertiesKeyValue(configFilePath, "serverName", session[HawkCDAgentProperties.ServerAddress]);
            }
            else
            {
                session.Log("Config file does not exists: {0}", configFilePath);
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
                if (IsJavaInstalled())
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

                if (IsPortInUse(port))
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
        public static ActionResult UpdateServiceAppConfig(Session session)
        {
            session.Log("Begin UpdateServerServiceAppConfig");
            try
            {
                var installerType = session[CommonProperties.InstallerType];
                var isntallDir = session[CommonProperties.InstallDir].TrimEnd('\\');
                var configFilePath = isntallDir + "\\HawkCDServiceWrapper.exe.config";

                session.Log("Installer type is:  {0}", installerType);

                if (!File.Exists(configFilePath))
                {
                    session.Log("File does not exist: {0}", configFilePath);
                    return ActionResult.Failure;
                }

                var jarFile = Directory.GetFiles(isntallDir, "*.jar").FirstOrDefault();
                jarFile = Path.GetFileName(jarFile);

                string[] values = null;

                if (installerType.Equals("Server", StringComparison.CurrentCultureIgnoreCase))
                {
                    values = new string[]
                    {
                        "StartProcessName", "java.exe",
                        "StartArgs", "-jar " + jarFile,
                        "EventSourceName", "HawkCDServer",
                        "ServiceName", GenerateServiceName("HawkCDServer"),
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
                        "ServiceName", GenerateServiceName("HawkCDAgent"),
                        "WorkingDirectory", isntallDir,
                    };
                }
                session.Log("Starting to update config: {0}", configFilePath);
                UpdateAppConfig(configFilePath, values);
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
                var installerType = session[CommonProperties.InstallerType];
                var isntallDir = session[CommonProperties.InstallDir].TrimEnd('\\');
                var configFilePath = isntallDir + "\\HawkCDServiceWrapper.exe.config";
                var serviceWrapperExe = isntallDir + "\\HawkCDServiceWrapper.exe";
                if (!File.Exists(configFilePath))
                {
                    session.Log("File does not exist: {0}", configFilePath);
                    return ActionResult.Failure;
                }

                var serviceName = GetServiceNameFromAppConfig(configFilePath);
                var installCommand = string.Format("sc create {0} displayname= \"HawkCD {1} Service\" binpath= \"{2}\" start= auto", serviceName, installerType, serviceWrapperExe);

                session.Log("Installing service: {0}", installCommand);
                ExecuteCommand(installCommand, session);

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
                var installerType = session[CommonProperties.InstallerType];
                var isntallDir = session[CommonProperties.InstallDir].TrimEnd('\\');
                var configFilePath = isntallDir + "\\HawkCDServiceWrapper.exe.config";

                if (!File.Exists(configFilePath))
                {
                    session.Log("File does not exist: {0}", configFilePath);
                    return ActionResult.Failure;
                }

                var serviceName = GetServiceNameFromAppConfig(configFilePath);
                var installCommand = string.Format("sc delete {0} ", serviceName);

                session.Log("Stopping service: {0}", serviceName);
                if (!ServiceUtils.StopService(serviceName))
                {
                    session.Log("Stopping service failed: {0}", serviceName);
                }

                session.Log("Uninstall service: {0}", installCommand);
                ExecuteCommand(installCommand, session);
            }

            catch (Exception ex)
            {
                session.Log("Exception: {0}", ex.ToString());
            }

            session.Log("Ended InstallService");

            return ActionResult.Success;
        }

        #endregion //Server Custom Actions 

        #region Private Methods
        private static bool IsJavaInstalled()
        {
            try
            {
                var paths = Environment.GetEnvironmentVariable("path").Split(';');
                foreach (var path in paths)
                {
                    if (File.Exists(path.TrimEnd('\\') + "\\java.exe"))
                    {
                        return true;
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception: {0}", ex);
            }
            return false;
        }

        private static void ExecuteCommand(string command, Session session, string workingDirectory = null)
        {
            // /c tells cmd that we want it to execute the command that follows and then exit.
            var procStartInfo = new ProcessStartInfo("cmd", "/c " + command);

            // The following commands are needed to redirect the standard output.
            // This means that it will be redirected to the Process.StandardOutput StreamReader.
            procStartInfo.RedirectStandardOutput = true;
            procStartInfo.UseShellExecute = false;
            procStartInfo.CreateNoWindow = true;
            if (!string.IsNullOrEmpty(workingDirectory))
                procStartInfo.WorkingDirectory = workingDirectory;

            var proc = new Process();
            proc.StartInfo = procStartInfo;
            session.Log("Start executing command: " + procStartInfo.Arguments);
            proc.Start();

            // Get the output into a string
            var result = proc.StandardOutput.ReadToEnd();

            // Display the command output.
            session.Log("Command Output: " + result);
            session.Log("Command Error Code: {0} - {1}", proc.ExitCode, TryGetMessageForExitCode(proc.ExitCode));
        }

        private static string TryGetMessageForExitCode(int exitCode)
        {
            try
            {
                return new System.ComponentModel.Win32Exception(exitCode).Message;
            }
            catch { }

            return string.Empty;
        }

        private static void ReplacePropertiesKeyValue(string filePath, string key, string value)
        {
            var content = File.ReadAllText(filePath);
            content = Regex.Replace(content, string.Format(@"{0}\s?=.*[^\n\r]", key), string.Format("{0}={1}", key, value));
            File.WriteAllText(filePath, content);
        }

        private static void ReplaceYamlKeyValue(string filePath, string key, string value)
        {
            var content = File.ReadAllText(filePath);
            content = Regex.Replace(content, string.Format(@"{0}\s?:.*[^\n\r]", key), string.Format("{0}: {1}", key, value));
            File.WriteAllText(filePath, content);
        }

        private static bool IsPortInUse(int port)
        {
            IPGlobalProperties ipGlobalProperties = IPGlobalProperties.GetIPGlobalProperties();
            TcpConnectionInformation[] tcpConnInfoArray = ipGlobalProperties.GetActiveTcpConnections();
            foreach (TcpConnectionInformation tcpi in tcpConnInfoArray)
            {
                if (tcpi.LocalEndPoint.Port == port)
                {
                    return true;
                }
            }

            foreach (IPEndPoint tcpi in ipGlobalProperties.GetActiveTcpListeners())
            {
                if (tcpi.Port == port)
                {
                    return true;
                }
            }

            return false;
        }

        private static void UpdateAppConfig(string appConfigPath, params string[] keyValueArray)
        {
            ExeConfigurationFileMap configMap = new ExeConfigurationFileMap();
            configMap.ExeConfigFilename = appConfigPath;
            Configuration config = ConfigurationManager.OpenMappedExeConfiguration(configMap, ConfigurationUserLevel.None);

            for (int i = 0; i < keyValueArray.Length; i += 2)
            {
                config.AppSettings.Settings[keyValueArray[i]].Value = keyValueArray[i + 1];
            }

            config.Save();
        }

        private static string GetServiceNameFromAppConfig(string appConfigPath)
        {
            ExeConfigurationFileMap configMap = new ExeConfigurationFileMap();
            configMap.ExeConfigFilename = appConfigPath;
            Configuration config = ConfigurationManager.OpenMappedExeConfiguration(configMap, ConfigurationUserLevel.None);

            return config.AppSettings.Settings["ServiceName"].Value;
        }

        private static string GenerateServiceName(string name)
        {
            if (ServiceUtils.IsServiceInstalled(name))
            {
                for (int i = 1; i < 1000; i++)
                {
                    name += i.ToString();
                    if (!ServiceUtils.IsServiceInstalled(name))
                        return name;

                }
            }

            return name;
        }
        #endregion //Private Methods
    }
}
