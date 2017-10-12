# SpringMVC + React 框架项目

## 整体项目

> 项目前端和后端需要分开开发(并不是代码分开,而是启动前端服务器和后台服务器,但是他们可以相互访问)

- 后台服务启动: 普通tomcat启动即可(推荐使用eclipse,方便调试部署),需要修改配置文件config.properties 中的 **project.source.path.root** 为当前项目路径.
- 前端服务启动: 修改 .roadhogrc.js 文件中的proxy(前端访问代理, 第18行,主要是端口)为启动的后台服务
```
     "/api/v1": {
       "target": "http://127.0.0.1:9090/Dashboard",
       "changeOrigin": true,
       "pathRewrite": { "^/api/v1" : "/api/v1" }
    }
```
使用 nodejs 的 **npm run dev** 启动前端服务,修改代码会自动重新部署(需要先安装nodejs和install相应的lib包,步骤见 **前端** 说明)

> 项目前端和后台可以分离部署也可合并部署

- 合并部署,使用 **mvn clean install** 即可将前端代码编译打包到war中,直接部署到 tomcat即可.
- 单独部署,后台作为单独的api服务器,使用 **mvn install** 即可打成war包,前端使用  **npm run build** 即可将前端代码打包到 dist文件夹,复制dist中文件到 nginx/apache等服务器就可以单独部署.



## 后端( SpringMVC )

基于 SpringMVC + Mybatis + swagger + Atomikos(多数据源事务管理) + maven 的框架


> 框架说明


>> 1. 后端整体框架是使用 springMVC 4.3.8 详细配置见 applicationContext.xml

>> 2. 数据库操作是使用 Mybatis 3.2.3 详细见 jdbc.xml配置

>> 3. 事物管理是使用 Atomikos 4.0.1, 可配置多数据源,详细见 jdbc.xml配置

>> 4. 包管理是使用 maven

>> 5. api访问测试是使用 swagger 0.9.5, 需要先使用 PUT方式的/api/v1/login 进行登录,才能访问api,否则401未授权


> 后端目录结构


>> 1. 控制器在 controller包下,所有控制器需要继承com.xh.common.controller.BaseController基础控制器,使用 com.xh.util.Utils.API_PREFIX 为RequestMapping前缀, 现有和前端配合的 登录/菜单/用户/权限 四个控制器.

>> 2. 数据源在 dao 包下,详细配置在jdbc.xml中,现配置了datasource1/datasource2/jsondatsource 三个数据源,其中datasource1/datasource2为两个数据库源(未使用,和jdbc.xml有关),jsondatsource(和jdbc.xml无关)为文件数据源,配合前端使用.

>> 3. 实体文件在 dto和entry包下,entry包下为数据库实体(表),dto包下为api访问需要的中间实体,他们都必须要继承 com.xh.common.dto.CommonDto 基础Dto(否则日志和api返回会异常).

>> 4. 服务在service包下,所有的服务都必须要继承 com.xh.common.service.BaseService 基础服务. 现有和前端配合的 登录/菜单/用户/权限 四个服务及其接口

>> 5. 工具类在util包下,现有base64加密工具;config属性文件读取工具;接口代理工具;类反射工具;字符串/集合/常量综合工具Util类;文件工具类在vfs子包下,com.xh.vfs.WebVFS类可以处理本地文件和远程文件(带协议头);xml工具类在xml包下,com.xh.xml.XMLHandler类可以处理xml文件或字符串,并对dom解析;httpUtil工具等

>> 6. 公共类在common包下,里面包含控制器,服务等所有公共基类

>> 7. 扩展类在ext包下,里面包含swagger配置类,异常处理类,过滤器类,切片类等功能扩展类


## 前端( React Antd )

基于 React + antd + dva + roadhog 的框架


> 开源文档


-  前端开源源码: [https://github.com/zuiidea/antd-admin](https://github.com/zuiidea/antd-admin)

-  dva框架知识地图 [https://github.com/dvajs/dva-knowledgemap](https://github.com/dvajs/dva-knowledgemap)

-  dva框架概念文档 [https://github.com/dvajs/dva/blob/master/docs/Concepts_zh-CN.md](https://github.com/dvajs/dva/blob/master/docs/Concepts_zh-CN.md)

-  dva框架API [https://github.com/dvajs/dva/blob/master/docs/API_zh-CN.md](https://github.com/dvajs/dva/blob/master/docs/API_zh-CN.md)

-  ant-design组件官网[https://ant.design/docs/react/introduce-cn](https://ant.design/docs/react/introduce-cn)

-  roadhog官网[https://github.com/sorrycc/roadhog](https://github.com/sorrycc/roadhog)

-  react官网 [https://github.com/facebook/react](https://github.com/facebook/react)



> 特性


-   基于[react](https://github.com/facebook/react)，[ant-design](https://github.com/ant-design/ant-design)，[dva](https://github.com/dvajs/dva)，[Mock](https://github.com/nuysoft/Mock) 企业级后台管理系统最佳实践

-   基于Antd UI 设计语言，提供后台管理系统常见使用场景

-   基于[dva](https://github.com/dvajs/dva)动态加载 Model 和路由，按需加载

-   使用[roadhog](https://github.com/sorrycc/roadhog)本地调试和构建，其中Mock功能实现脱离后端独立开发

-   浅度响应式设计


> 目录结构

   

```bash

    ├── /dist/           # 项目输出目录

    ├── /src/            # 项目源码目录

    │ ├── /public/       # 公共文件，编译时copy至dist目录

    │ ├── /components/   # UI组件及UI相关方法

	│ │ ├── skin.less    # 全局样式

	│ │ └── vars.less    # 全局样式变量

	│ ├── /routes/       # 路由组件

	│ │ └── app.js       # 路由入口

	│ ├── /models/       # 数据模型

	│ ├── /services/     # 数据接口

	│ ├── /themes/       # 项目样式

	│ ├── /mock/         # 数据mock

	│ ├── /utils/        # 工具函数

	│ │ ├── config.js    # 项目常规配置

	│ │ ├── menu.js      # 菜单及面包屑配置

	│ │ ├── config.js    # 项目常规配置

	│ │ ├── request.js   # 异步请求函数

	│ │ └── theme.js     # 项目需要在js中使用到样式变量

	│ ├── route.js       # 路由配置

	│ ├── index.js       # 入口文件

	│ └── index.html    

	├── package.json     # 项目信息息

	├── .eslintrc        # Eslint配置

	└── .roadhogrc.js    # roadhog配置

```


> 快速开始


>> 1.进入前端源码目录 Dashboard\src\main\webapp\frontend-src


>> 2.首次

```bash

	#开始前请确保没有安装roadhog、webpack到NPM全局目录

	npm i 或者 yarn install

```


>> 3.开发

```bash

	npm run build:dll #第一次npm run dev时需运行此命令，使开发时编译更快

	npm run dev

    打开 http://localhost:8000 开发调试

```


>> 4.构建(**可忽略,maven install 会自动调用**)

```bash

	npm run build

```


>> 5.代码检测(**可忽略**))

```bash

	npm run lint

```


> 开发


- src/router.js 为全局路由文件,新建路由需要增加文件中routes数组

- src/routes/XXX 新建路由component

- src/models/XXX 新建路由model


    **ps. 简易开发 ,在开发环境,运行npm run dev,在menu管理中增加菜单并自动生成模板,会自动增加路由和相关文件,只有在开发环境才能使用,后端需要配置project.source.path.root属性**


> 发布


 在根目录Dashboard下,运行 mvn clean install ,会将前端代码进行构建打包并复制到Dashboard\src\main\webapp\frontend中并打入war中,可直接放入tomcat等环境直接使用

