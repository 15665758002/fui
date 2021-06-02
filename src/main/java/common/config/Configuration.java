package common.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"classpath:env.properties"})
public interface Configuration extends Config {

    @Key("Domain")
    String Domain();

    @Key("loginUrl")
    String loginUrl();

    @Key("isLoginurl")
    String isLoginurl();

    @Key("username")
    String getUsername();

    @Key("password")
    String getPassword();

    @Key("dingdingUrl")
    String getdingUrl();

    @Key("jenkinsUrl")
    String getJenkinsUrl();

    @Key("configDriver")
    String getConfigDriver();

}
