using System;
using System.Configuration;

namespace HawkCDServiceWrapper
{
    public class ArgumentsUtils
    {
        public static string StartProcessName { get; private set; }
        public static string StartArgs { get; private set; }
        public static string StopArgs { get; private set; }
        public static string EventSourceName { get; private set; }
        public static string WorkingDirectory { get; private set; }
        public static bool ReadArgs()
        {
            try
            {
                StartProcessName = ConfigurationManager.AppSettings["StartProcessName"];
                StartArgs = ConfigurationManager.AppSettings["StartArgs"];
                EventSourceName = ConfigurationManager.AppSettings["EventSourceName"];
                WorkingDirectory = ConfigurationManager.AppSettings["WorkingDirectory"];
                StopArgs = ConfigurationManager.AppSettings["StopArgs"];

            }
            catch (Exception ex)
            {
                return false;
            }

            return true;
        }
    }
}
