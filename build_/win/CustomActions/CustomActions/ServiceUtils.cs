using System;
using System.Linq;
using System.ServiceProcess;

namespace CustomActions
{
    public class ServiceUtils
    {
        public static string Error { get; private set; }

        public static bool IsServiceInstalled(string serviceName)
        {
            return ServiceController.GetServices().FirstOrDefault(x => x.ServiceName.Equals(serviceName, StringComparison.CurrentCultureIgnoreCase)) != null;
        }

        public static ServiceController GetService(string serviceName)
        {
            return ServiceController.GetServices().FirstOrDefault(x => x.ServiceName.Equals(serviceName, StringComparison.CurrentCultureIgnoreCase));
        }

        public static bool StartService(string serviceName)
        {
            try
            {
                var service = GetService(serviceName);

                if (service.Status != ServiceControllerStatus.Running)
                    service.Start();

                service = GetService(serviceName);

                return service.Status == ServiceControllerStatus.Running;
            }
            catch (Exception ex)
            {
                Error = ex.ToString();
                return false;
            }
        }

        public static bool StopService(string serviceName)
        {
            try
            {
                var service = GetService(serviceName);

                if (service.Status != ServiceControllerStatus.Stopped)
                    service.Stop();

                service = GetService(serviceName);

                return service.Status == ServiceControllerStatus.Stopped;
            }
            catch (Exception ex)
            {
                Error = ex.ToString();
                return false;
            }
        }
    }
}
