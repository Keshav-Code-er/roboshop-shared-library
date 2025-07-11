#!groovy

def decidePipeline(Map configMap){
      application = configMap.get("application")
      //# here we are getting modeJSVM

      switch(application) {
      case 'nodeJSVM':
            NodeJSVMCI(configMap)
            break
      case  'JavaVM':
            JavaVMCI(configMap)      
            break
      default:
            error "Unreocgnised application"
            break
      }
}