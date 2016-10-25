using Microsoft.Deployment.WindowsInstaller;
using System;
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Net.NetworkInformation;
using System.Text.RegularExpressions;

namespace CustomActions
{
    public enum InstallerType { Server, Agent }

    class Utils
    {
        public static bool IsJavaInstalled()
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

        public static void ExecuteCommand(string command, Session session, string workingDirectory = null)
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

        public static string TryGetMessageForExitCode(int exitCode)
        {
            try
            {
                return new System.ComponentModel.Win32Exception(exitCode).Message;
            }
            catch { }

            return string.Empty;
        }

        public static void ReplacePropertiesKeyValue(string filePath, string key, string value)
        {
            var content = File.ReadAllText(filePath);
            content = Regex.Replace(content, string.Format(@"{0}\s?=.*[^\n\r]", key), string.Format("{0}={1}", key, value));
            File.WriteAllText(filePath, content);
        }

        public static void ReplaceYamlKeyValue(string filePath, string key, string value)
        {
            var content = File.ReadAllText(filePath);
            content = Regex.Replace(content, string.Format(@"{0}\s?:.*[^\n\r]", key), string.Format("{0}: {1}", key, value));
            File.WriteAllText(filePath, content);
        }

        public static bool IsPortInUse(int port)
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

        public static void UpdateAppConfig(string appConfigPath, params string[] keyValueArray)
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

        public static string GetServiceNameFromAppConfig(string appConfigPath)
        {
            ExeConfigurationFileMap configMap = new ExeConfigurationFileMap();
            configMap.ExeConfigFilename = appConfigPath;
            Configuration config = ConfigurationManager.OpenMappedExeConfiguration(configMap, ConfigurationUserLevel.None);

            return config.AppSettings.Settings["ServiceName"].Value;
        }

        public static string GenerateServiceName(string name)
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

        public static InstallerType GetInstallerType(Session session)
        {
            if (session[CommonProperties.InstallerType].Equals(Constants.ServerInstallerType, StringComparison.CurrentCultureIgnoreCase))
                return InstallerType.Server;
            else
                return InstallerType.Agent;
        }

        public static string GetPathToFileInInstalDir(Session session, string fileSubPath)
        {
            return session[CommonProperties.InstallDir].TrimEnd('\\') + "\\" + fileSubPath.TrimStart('\\');
        }

        public static string GetInstalDir(Session session)
        {
            return session[CommonProperties.InstallDir].TrimEnd('\\');
        }

        public static void CreateFileBackup(string filePath, Session session)
        {
            var backupFile = filePath + Constants.BackupFileExtension;

            if (File.Exists(filePath))
            {
                session.Log("Creating {0}", backupFile);
                File.Copy(filePath, backupFile, true);
                session.Log("Created {0}", backupFile);
            }
            else
            {
                session.Log("File does not exists: {0}", filePath);
            }
        }

        public static void ReplaceFileFromBackup(string filePath, Session session)
        {
            var backupFile = filePath + Constants.BackupFileExtension;

            if (File.Exists(backupFile))
            {
                session.Log("Deleting file {0}", filePath);
                File.Delete(filePath);
                session.Log("File was deleted {0}", filePath);
                session.Log("Renaming file {0} to {1}", backupFile, filePath);
                File.Move(backupFile, filePath);
                session.Log("Created {0}", filePath);
            }
            else
            {
                session.Log("Backup file does not exists: {0}", backupFile);
            }
        }
    }
}
