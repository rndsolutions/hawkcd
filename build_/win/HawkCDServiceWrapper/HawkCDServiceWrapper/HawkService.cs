using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;

namespace HawkCDServiceWrapper
{
    public partial class HawkService : ServiceBase
    {
        private const string DefaultEventSourceName = "HawkCDService";
        private static Process process;
        private static string startProcessExeFullPath;
        private static ServiceBase service;
        public HawkService()
        {
            service = this;

            InitializeComponent();
        }

        protected override void OnStart(string[] args)
        {
            try
            {
                if (!ArgumentsUtils.ReadArgs())
                {
                    if (!EventLog.SourceExists(DefaultEventSourceName))
                        EventLog.CreateEventSource(DefaultEventSourceName, "Application");

                    EventLog.WriteEntry(DefaultEventSourceName, "Invalid arguments. Stopping Service.");
                    Stop();

                }

                if (!EventLog.SourceExists(ArgumentsUtils.EventSourceName))
                    EventLog.CreateEventSource(ArgumentsUtils.EventSourceName, "Application");

                if (string.IsNullOrEmpty(LocateStartProcessExeFullPath()))
                {
                    EventLog.WriteEntry(ArgumentsUtils.EventSourceName, "Unable to locate start process: " + ArgumentsUtils.StartProcessName);
                    Stop();
                }

                EventLog.WriteEntry(ArgumentsUtils.EventSourceName, "Starting: " + ArgumentsUtils.StartProcessName + " with arguments: " + ArgumentsUtils.StartArgs);

                StartProcess();
            }
            catch (Exception ex)
            {
                EventLog.WriteEntry(DefaultEventSourceName, ex.ToString());
                EventLog.WriteEntry(DefaultEventSourceName, "Stopping Service.");
                Stop();
            }
        }

        protected override void OnStop()
        {
            if (process != null)
            {
                if (!process.HasExited)
                    process.Kill();
            }
        }

        private static void StartProcess()
        {
            var startInfo = new ProcessStartInfo
            {
                FileName = startProcessExeFullPath,
                Arguments = ArgumentsUtils.StartArgs,
                WindowStyle = ProcessWindowStyle.Hidden,
                CreateNoWindow = true,
                UseShellExecute = false,
                RedirectStandardError = true
            };


            if (!string.IsNullOrEmpty(ArgumentsUtils.WorkingDirectory))
                startInfo.WorkingDirectory = ArgumentsUtils.WorkingDirectory;

            process = new Process();
            process.StartInfo = startInfo;
            process.Exited += Process_Exited;
            process.Start();
        }

        private static void Process_Exited(object sender, EventArgs e)
        {
            EventLog.WriteEntry(DefaultEventSourceName, "process has exited.");
            service.Stop();
        }

        public static string LocateStartProcessExeFullPath()
        {
            var process = ArgumentsUtils.StartProcessName;
            if (!process.EndsWith(".exe", StringComparison.CurrentCultureIgnoreCase))
                process = process + ".exe";

            foreach (var item in Environment.GetEnvironmentVariable("path").Split(';'))
            {
                var exePath = item.TrimEnd('\\') + "\\" + process;

                if (File.Exists(exePath))
                {
                    startProcessExeFullPath = exePath;
                    return exePath;
                }
            }

            return string.Empty;
        }

    }
}
