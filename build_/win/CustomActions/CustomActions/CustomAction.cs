using Microsoft.Deployment.WindowsInstaller;
using System.Diagnostics;
using System.IO;
using System.Text.RegularExpressions;

namespace CustomActions
{
    public class CustomActions
    {
        #region Server Custom Actions 

        [CustomAction]
        public static ActionResult InstallRedis(Session session)
        {
            session.Log("Begin InstallRedis");

            var isntallDir = session[HawkCDServerProperties.InstallDir].TrimEnd('\\');
            var redisFolder = isntallDir + "\\redis";

            ExecuteCommand("redis-server --service-install redis.windows-service.conf", session, redisFolder);
            ExecuteCommand("redis-server --service-start", session, redisFolder);

            session.Log("Ended InstallRedis");

            return ActionResult.Success;
        }

        [CustomAction]
        public static ActionResult UninstallRedis(Session session)
        {
            session.Log("Begin UninstallRedis");

            var isntallDir = session[HawkCDServerProperties.InstallDir].TrimEnd('\\');
            var redisFolder = isntallDir + "\\redis";

            ExecuteCommand("redis-server --service-stop", session, redisFolder);
            ExecuteCommand("redis-server --service-uninstall", session, redisFolder);

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

        #endregion //Server Custom Actions 

        #region Private Methods

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

        private static void ReplaceYamlKeyValue(string filePath, string key, string value)
        {
            var content = File.ReadAllText(filePath);
            content = Regex.Replace(content, string.Format(@"{0}\s?:.*", key), string.Format("{0}: {1}", key, value));
            File.WriteAllText(filePath, content);
        }

        private static void ReplacePropertiesKeyValue(string filePath, string key, string value)
        {
            var content = File.ReadAllText(filePath);
            content = Regex.Replace(content, string.Format(@"{0}\s?=.*", key), string.Format("{0}={1}", key, value));
            File.WriteAllText(filePath, content);
        }

        #endregion //Private Methods
    }
}
