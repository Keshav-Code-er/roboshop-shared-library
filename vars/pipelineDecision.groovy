#!groovy

def decidePipeline(Map configMap){
      application = configMap.get("application")
      //# here we are getting modeJSVM

      switch(application) {
      case 'nodeJSVM':
            nodeJSVMCI(configMap)
            break
      case  'JavaVM':
            javaVMCI(configMap)      
            break
      default:
            error "Unreocgnised application"
            break
      }
}