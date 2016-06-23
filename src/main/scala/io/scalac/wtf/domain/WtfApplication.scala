package io.scalac.wtf.domain

trait WtfApplication extends App
  with AkkaDependancy
  with DatabaseDependency
  with UserRoutes