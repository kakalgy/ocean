/**
 * @apiNote 桥接模式
 * @author gyli
 * @version 1.0
 * date: 2018-10-11 21:48
 */
package com.yusj.dolphin.bridge;

/**
 * 桥接模式（Bridge)是一种结构型设计模式。Bridge模式基于类的最小设计原则，通过使用封装、聚合及继承等行为让不同的类承担不同的职责。
 * 它的主要特点是把抽象(Abstraction)与行为实现(Implementation)分离开来，从而可以保持各部分的独立性以及应对他们的功能扩展。
 *
 * <ul>
 *     <li>1.Client 调用端: 这是Bridge模式的调用者。</li>
 *     <li>2.抽象类(Abstraction): 抽象类接口（接口这货抽象类）维护队行为实现（implementation）的引用。它的角色就是桥接类。</li>
 *     <li>3.Refined Abstraction: 这是Abstraction的子类。</li>
 *     <li>4.Implementor: 行为实现类接口（Abstraction接口定义了基于Implementor接口的更高层次的操作）</li>
 *     <li>5.ConcreteImplementor: Implementor的子类</li>
 * </ul>
 */

/**
 *<ul>
 *     <li>https://www.cnblogs.com/lixiuyu/p/5923160.html</li>
 *</ul>
 */