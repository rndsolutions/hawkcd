using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;

namespace HawkCDServiceWrapper
{
    static class Program
    {
        //http://stackoverflow.com/questions/3663331/creating-a-service-with-sc-exe-how-to-pass-in-context-parameters
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {


            ServiceBase[] ServicesToRun;
            ServicesToRun = new ServiceBase[]
            {
                new HawkService()
            };
            ServiceBase.Run(ServicesToRun);
        }
    }
}
